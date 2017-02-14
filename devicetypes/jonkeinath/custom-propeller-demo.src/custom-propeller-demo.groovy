/**
 *  Custom Device type for Custom Propeller Demo
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Custom Propeller Demo", namespace: "JonKeinath", author: "Jon@electronpotential.com") {
        capability "Refresh"
        capability "Polling"
        capability "Sensor"
        capability "Switch"
        
    	fingerprint endpointId: "38", profileId: "0104", deviceId: "0002", deviceVersion: "00", inClusters: "0000, 0006"    
	}

	// simulator metadata
	simulator {
    }

	// UI tile definitions
	tiles {

		standardTile("switch1", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "Switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "Switch.off", icon: "st.switches.switch.on", backgroundColor: "#0044ff"
		}
        
        standardTile("switch2", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "Switch.on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "Switch.off2", icon: "st.switches.switch.on", backgroundColor: "#00ffff"
		}
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
		main (["switch1","switch2"])
		details (["switch1", "switch2", "refresh"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
    log.debug "Parse description $description"
    def name = null
    def value = null
 
    if (description?.startsWith("catchall: 0104 0006 38")) {
        log.debug "On/Off command received from EP 1"
        if (description?.endsWith("01 01 0000001000")){
        	name = "switch"
            value = "off"}
        else if (description?.endsWith("01 01 0000001001")){
        	name = "switch"
            value = "on"}                        
    }  

	def result = createEvent(name: name, value: value)
    log.debug "Parse returned ${result?.descriptionText}"
    return result
}

// Commands to device

def on() {
	log.debug "Relay 1 on()"
	sendEvent(name: "switch1", value: "on")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x1 {}"
}

def on2() {
	log.debug "Relay 2 on()"
	sendEvent(name: "switch2", value: "on")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x3 {}"
}

def off() {
	log.debug "Relay 1 off()"
	sendEvent(name: "switch1", value: "off")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x0 {}"
    //"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x4 {}"
}

def off2() {
	log.debug "Relay 2 off()"
	sendEvent(name: "switch2", value: "off")
	"st cmd 0x${device.deviceNetworkId} 0x38 0x0006 0x4 {}"
}

def poll(){
	log.debug "Poll is calling refresh"
	refresh()
}

def refresh() {
	log.debug "sending refresh command"
    def cmd = []	
    cmd << "st rattr 0x${device.deviceNetworkId} 0x38 0x0006 0x0000"	// Read on / off attribute at End point 0x38
    cmd
}