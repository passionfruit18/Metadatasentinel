package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.springframework.web.multipart.MultipartFile

@GrailsCompileStatic
class RecordFileCommand implements Validateable {
    MultipartFile csvFile
    Integer batchSize = 100
    String datasetName

    static constraints = {
        datasetName nullable: false, blank: false
        batchSize nullable: false
        csvFile  validator: { MultipartFile val, RecordFileCommand obj ->
            if ( val == null ) {
                return false
            }
            if ( val.empty ) {
                return false
            }
            allowedExtensions().any { String extension ->
                val.originalFilename?.toLowerCase()?.endsWith(extension)
            }
        }
    }

    static List<String> allowedExtensions() {
        ['csv', 'xlsx']
    }

}