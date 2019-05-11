/**
 *  Arlo Image/Clip Refresh
 *
 *  Copyright 2018 Mavrrick
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
    name: "Arlo Image/Clip Refresh",
    namespace: "Mavrrick",
    author: "Mavrrick",
    description: "Create a 5 second clip to refresh image tile.",
    category: "My Apps",
	iconUrl: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX2Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg",
	iconX3Url: "https://farm9.staticflickr.com/8632/16461422990_e5121d68ee_o.jpg"
    )

import groovy.time.TimeCategory 

preferences {
	section("Setup") {
		input "cameras", "capability.videoCapture", multiple: true
        input name: "clipLength", type: "number", title: "Clip Length", description: "Please enter the length of each recording", required: true, range: "5..120"
        input "frequencyUnit", "enum", options: ["mins", "hours"], required: true, defaultValue: "hours", title: "Unit for frequency"
        input name: "frequency", type: "number", title: "Frequency to refresh cameras", description: "Please specify how often you would like to refresh cameras", required: true
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
	if (frequencyUnit?.equals("hours")) {
		schedule("0 0 0/${frequency} * * ?", arloRefresh)
		}
        else {
	schedule("0 0/${frequency} 0 * * ?", arloRefresh)
					}
//	schedule("0 0 0/${frequency} * * ?", arloRefresh)
}

def arloRefresh() {	
	log.debug "$evt.name: $evt.value"
	log.debug "Refreshing cameras with ${clipLength} second capture"
    Date start = new Date()
    Date end = new Date()
    use( TimeCategory ) {
    	end = start + clipLength.seconds
 	}
    log.debug "Capturing..."
    cameras.capture(start, start, end)
}