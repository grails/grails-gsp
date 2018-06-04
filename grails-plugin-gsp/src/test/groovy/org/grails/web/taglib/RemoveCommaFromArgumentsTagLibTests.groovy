package org.grails.web.taglib

import grails.artefact.Artefact
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ConcatTagLib)
class RemoveCommaFromArgumentsTagLibTests extends Specification {

    void 'test invoke tag lib with multiple params with commas'() {
        expect:
        applyTemplate('<g:concatAllAttrsKeys one="1" two="2", three="3" four="4" five="5" />') == 'onetwothreefourfive'

        and:
        applyTemplate('<g:concatAllAttrsValues one="1" two="2", three="3" four="4" five="5" />') == '12345'
    }
}

@Artefact('TagLib')
class ConcatTagLib {
    Closure concatAllAttrsKeys = { attrs, body ->
        attrs.each { attr ->
            out << attr.key
        }
    }

    Closure concatAllAttrsValues = { attrs, body ->
        attrs.each { attr ->
            out << attr.value
        }
    }
}
