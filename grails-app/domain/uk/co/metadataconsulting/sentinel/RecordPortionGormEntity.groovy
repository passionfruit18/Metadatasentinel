package uk.co.metadataconsulting.sentinel

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class RecordPortionGormEntity {

    String header
    String name
    String value
    ValidationStatus status = ValidationStatus.NOT_VALIDATED
    String reason
    Integer numberOfRulesValidatedAgainst
    Date lastUpdated

    static belongsTo = [record: RecordGormEntity]

    static constraints = {
        numberOfRulesValidatedAgainst min: 0, nullable: false
        header nullable: true, blank: true
        name nullable: true, blank: true
        value nullable: false, blank: false
        status nullable: false
        reason nullable: true
    }

    static mapping = {
        table 'recordportion'
        reason type: 'text'
        sort 'header'
    }
}