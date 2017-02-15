/**
 *  
 *  Propeller Dual LED Output
 *
 *  Copyright 2017 Jon Keinath
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
 

preferences {
  input ("delayMillis", "number", title: "Command delay in ms", 
    description: "Time in milliseconds to delay sending multiple commands.", defaultValue: 0,
    required: false, range: "0..5000")
}

metadata {
    definition (name: "Propeller Dual LED Output", namespace: "JonKeinath", author: "jon@electronpotential.com") {
        capability "Polling"
        capability "Refresh"
        capability "Switch"

        attribute "switch1", "string"
        attribute "switch2", "string"

        command "on1"
        command "off1"
        command "on2"
        command "off2"

        fingerprint deviceId: "38", inClusters: "0000, 0006"
    }

    simulator {
    }

    tiles(scale:2){
        standardTile("switch1", "device.switch1", width: 2, height: 2, canChangeIcon: false) {
            state "on", label: '${name}', action: "off1", icon: "st.Lighting.light11", backgroundColor: "#0044ff"
            state "off", label: '${name}', action: "on1", icon: "st.Lighting.light13", backgroundColor: "#ffffff"
        }
        standardTile("switch2", "device.switch2", width: 2, height: 2, canChangeIcon: false) {
            state "on", label: '${name}', action: "off2", icon: "st.Lighting.light11", backgroundColor: "#0044ff"
            state "off", label: '${name}', action: "on2", icon: "st.Lighting.light13", backgroundColor: "#ffffff"
        }
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["switch1", "switch2"])
        details(["switch1","switch2","refresh"])
    }
}

def parse(String description) {
    log.debug "Parsing '${description}'"
    def result = []
    def cmd = zwave.parse(description)
    if (cmd) {
        result += zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    return result
}

def poll() {
    log.debug "Executing 'poll'"
	delayBetween([
    	zwave.switchBinaryV1.switchBinaryGet().format(),
    	zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
	], settings.delayMillis)
}

def on1() {
    log.debug "ZV 1 on()"
	sendEvent(name: "switch1", value: "ON")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x1 {}"
}

def off1() {
    log.debug "ZV 1 off()"
	sendEvent(name: "switch1", value: "off")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x0 {}"
}

def on2() {
    log.debug "ZV 2 on()"
	sendEvent(name: "switch2", value: "on")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x3 {}"
}

def off2() {
    log.debug "ZV 2 off()"
	sendEvent(name: "switch2", value: "off")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x4 {}"
}