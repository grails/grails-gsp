/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.plugins.web

import org.grails.plugins.web.taglib.RenderSitemeshTagLib
import org.grails.plugins.web.taglib.SitemeshTagLib

import grails.plugins.Plugin
import grails.util.GrailsUtil
import groovy.util.logging.Slf4j

/**
 * Plugin responsible for SiteMesh 2 specific configuration.
 */
@Slf4j
class Sitemesh2GrailsPlugin extends Plugin {
    def grailsVersion = "6.0.0 > *"
    def dependsOn = [core: GrailsUtil.getGrailsVersion(), i18n: GrailsUtil.getGrailsVersion()]
    def observe = ['controllers']
    def loadBefore = ['gsp']

    def providedArtefacts = [
        RenderSitemeshTagLib,
        SitemeshTagLib
    ]
}
