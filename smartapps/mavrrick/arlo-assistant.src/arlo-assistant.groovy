/**
 *  Arlo Assistant
 *
 *  Copyright 2018 CRAIG KING
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Arlo Assistant",
    namespace: "Mavrrick",
    author: "CRAIG KING",
    description: "Help Manage Arlo Cameras from Smartthings",
    category: "Safety & Security",
	iconUrl: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX2Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX3Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
    singleInstance: true)

/* 
*
* Prerelease v.0.1
* This is a prerelease of ArloAssistant to provide programs already created and ready to use.
*
*/


preferences {
    // The parent app preferences are pretty simple: just use the app input for the child app.
    page(name: "mainPage", title: "Arlo Assistant", install: true, uninstall: true,submitOnChange: true) {
    	section ("Current state"){
        paragraph "This is a early release version. More features will be coming"
         }
        section ("Arlo integration apps"){
            app(name: "arloTriggerRecord", appName: "Arlo Triggered Record", namespace: "Mavrrick", title: "Create a triggered event that will create recording", multiple: true)
            app(name: "arloTriggerRecordRepeat", appName: "Arlo Triggered Repeat Recorder", namespace: "Mavrrick", title: "Create a triggered event that will record until the event is over", multiple: true)            
            }
        section ("Arlo Event changes"){
            app(name: "arloImageRefresh", appName: "Arlo Image/Clip Refresh", namespace: "Mavrrick", title: "Generate a clip to refresh the image tile on the camera", multiple: true)
		}
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    // nothing needed here, since the child apps will handle preferences/subscriptions
    // this just logs some messages for demo/information purposes
    log.debug "there are ${childApps.size()} child smartapps"
    childApps.each {child ->
        log.debug "child app: ${child.label}"
    }
}