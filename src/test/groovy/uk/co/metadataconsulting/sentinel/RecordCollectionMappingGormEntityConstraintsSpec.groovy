package uk.co.metadataconsulting.sentinel

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class RecordCollectionMappingGormEntityConstraintsSpec extends Specification
        implements DomainUnitTest<RecordCollectionMappingGormEntity> {

    void 'test header cannot be null'() {
        when:
        domain.header = null

        then:
        !domain.validate(['header'])
    }

    void 'test gormUrl can be null'() {
        when:
        domain.gormUrl = null

        then:
        domain.validate(['gormUrl'])
    }

    void 'test dataModelId can be null'() {
        when:
        domain.dataModelId = null

        then:
        domain.validate(['dataModelId'])
    }
}
