package uk.co.metadataconsulting.sentinel

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.hibernate.SessionFactory
import org.hibernate.Session
import org.springframework.context.MessageSource
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRule
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

@Slf4j
@CompileStatic
class CsvImportService implements CsvImport, Benchmark {

    CsvImportProcessorService csvImportProcessorService

    DlrValidatorService dlrValidatorService

    RuleFetcherService ruleFetcherService

    RecordGormService recordGormService

    RecordCollectionGormService recordCollectionGormService

    MessageSource messageSource

    SessionFactory sessionFactory

    @Override
    void save(List<String> gormUrls, InputStream inputStream, Integer batchSize) {
        RecordCollectionGormEntity recordCollection = recordCollectionGormService.save()


        Map<String, ValidationRules> gormUrlsRules = [:]
        for ( String gormUrl : gormUrls ) {
            ValidationRules validationRules = ruleFetcherService.fetchValidationRules(gormUrl)
            if ( validationRules ) {
                gormUrlsRules[gormUrl] = validationRules
            }
        }

        long duration = benchmark {
            csvImportProcessorService.processInputStream(inputStream, batchSize) { List<List<String>> valuesList ->
                save(recordCollection, valuesList, gormUrls, gormUrlsRules)
                cleanUpGorm()
            }
        }
        log.info "execution batchSize: ${batchSize} took ${duration} ms"
    }

    int indexOfGormUrl(List<String> gormUrls, String gormUrl) {
        for ( int i = 0; i < gormUrls.size(); i++ ) {
            if ( gormUrls[i] == gormUrl ) {
                return i
            }
        }
        return -1
    }

    String[] valuesOfGormUrls(List<String> ruleGormUrls, List<String> gormUrls, List<String> values) {
        List result = []
        for ( String gormUrl : ruleGormUrls ) {
            int index = indexOfGormUrl(gormUrls, gormUrl)
            if ( index != -1 ) {
                result << values[index]
            }
        }
        result as String[]
    }

    void save(RecordCollectionGormEntity recordCollection, List<List<String>> valuesList, List<String> gormUrls, Map<String, ValidationRules> gormUrlsRules) {

        for ( List<String> values : valuesList ) {
            List<RecordPortion> recordPortionList = []

            for ( int i = 0; i < values.size(); i++ ) {
                String value = values[i]
                String gormUrl = gormUrls[i]
                ValidationRules validationRules = gormUrlsRules[gormUrl]
                if ( validationRules ) {
                    String reason
                    boolean isValid = true
                    for ( ValidationRule validationRule : validationRules.rules ) {
                        String[] ruleValues = valuesOfGormUrls(validationRule.gormUrls, gormUrls, values)
                        isValid = dlrValidatorService.validate(ruleValues, validationRule.rule)
                        if ( !isValid ) {
                            reason = validationRule.name
                            break
                        }
                    }
                    String name = validationRules.name
                    recordPortionList << new RecordPortion(name: name, gormUrl: gormUrl, value: value, valid: isValid, reason: reason)
                } else {
                    recordPortionList << new RecordPortion(value: value, valid: true)
                }

            }
            recordGormService.save(recordCollection, recordPortionList)
        }
    }

    def cleanUpGorm() {
        Session session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }
}