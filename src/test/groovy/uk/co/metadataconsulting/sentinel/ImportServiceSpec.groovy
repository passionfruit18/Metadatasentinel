package uk.co.metadataconsulting.sentinel

import grails.testing.services.ServiceUnitTest
import org.modelcatalogue.core.scripting.ValidatingImpl
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRules

class ImportServiceSpec extends Specification implements ServiceUnitTest<ImportService> {

    def "validating rule is processed"() {
        given:
        service.validateRecordPortionService = Mock(ValidateRecordPortionService)
        String gormUrl = 'gorm://org.modelcatalogue.core.EnumeratedType:250'
        String value = 'yellow'
        String header = 'Color'
        List<String> values = ["RW6A06647291","yellow"]
        ValidationRules validationRules = new ValidationRules(validating: new ValidatingImpl(explicitRule: "x == null || x in ['red', 'blue']"))
        MappingMetadata metadata = new MappingMetadata()
        metadata.with {
            headerLineList = ['RadiologicalAccessionNumber', 'Color']
            gormUrls = ['gorm://org.modelcatalogue.core.DataElement:77', 'gorm://org.modelcatalogue.core.EnumeratedType:250']
            gormUrlsRules = [('gorm://org.modelcatalogue.core.EnumeratedType:250'): validationRules]
        }

        when:
        RecordPortion portion = service.recordPortionFromValue(gormUrl, value, header, values, metadata)

        then:
        portion.gormUrl == gormUrl
        portion.numberOfRulesValidatedAgainst == 1
        portion.reason != null
        !portion.valid
    }

    @Unroll
    def "headerAtIndex with values ( #headerLineList  #index ) does not throw exception"(List<String> headerLineList, int index) {
        when:
        MappingMetadata metadata = new MappingMetadata(headerLineList: headerLineList)
        service.headerAtIndex(metadata, index)

        then:
        noExceptionThrown()

        where:
        headerLineList | index
        null           | 0
    }
}