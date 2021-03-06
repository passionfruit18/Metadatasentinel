package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface RuleValidator {
    String validate(String name, String rule, Map<String, String> identifierToValue)
}
