/*  Copyright 2015 lgkahn
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   version 1.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
metadata {
  definition (name: "Smart Alarm Notification Device", namespace: "lgkapps", author: "Larry Kahn kahn@lgk.com") {
    capability "Notification"
    capability "Switch"
    
    command "onPhysical"
	command "offPhysical"
     attribute "armDisarm", "string"
     attribute "armStay", "string"
     attribute "armAway", "string"

    
   
  }

  simulator {
  }

  tiles (scale: 2){
    valueTile("status","device.status", width: 6, height: 4){  
        state "All Ok", label:'${name}', backgroundColor:"#4f9558"
        state "Water Alert", label:'${name}', backgroundColor:"#007f8f"
        state "Smoke/CO2 Alert", label:'${name}', backgroundColor:"#711100"
        state "Intrusion Alert", label:'${name}', backgroundColor:"#8a0707"
      }
      
      
    
     valueTile("armDisarm", "device.armDisarm", width: 2, height: 2) {
			state "unlite", label: 'Disarmed', backgroundColor: "#ffffff"
            state "lite", label: 'Disarmed', backgroundColor: "#4f9558"
	}	
      valueTile("armStay", "device.armStay",  width: 2, height: 2) {
			state "unlite", label:'Armed Stay',icon:"",backgroundColor: "#ffffff"
            state "lite", label:'Armed Stay',backgroundColor: "#4f9558"
	}	
      valueTile("armAway", "device.armAway",  width: 2, height: 2) {
			state "unlite", label:'Armed Away',icon:"",backgroundColor: "#ffffff"
            state "lite", label:'Armed Away',backgroundColor: "#4f9558"
	}	
    
    
    valueTile("armStatus", "device.armStatus", inactiveLabel: false, width: 4, height: 1) {
			state "default", label:'Arming Status: ${currentValue}',icon:""
	}		
    valueTile("lastAlert", "device.lastAlert", inactiveLabel: false, width: 4, height: 1) {
			state "default", label:'Last Alert: ${currentValue}',icon:""
            }
    valueTile("lastAlertType", "device.lastAlertType", inactiveLabel: false, width: 4, height: 1) {
			state "default", label:'Last Type: ${currentValue}',icon:""
		
    }
 
		valueTile("dismissSwitch", "device.switch", width: 2, height: 2, canChangeIcon: true) { 
 			state "off", label: '', action: "", icon: "", backgroundColor: "#ffffff" 
 			state "on", label: 'Dismiss\nAlert', action: "switch.off", icon: "", backgroundColor: "#8a0707" 
 		}  		

}
    main (["status"])
    details(["status","armDisarm","armStay","armAway","lastAlert","lastAlertType","dismissSwitch"])
}


def parse(String description) {
log.debug "in parse desc = $description"
	def pair = description.split(":")
	createEvent(name: pair[0].trim(), value: pair[1].trim())
}

def installed() {
log.debug "in installed"
sendEvent(name: "status" , value: "All Ok")
sendEvent(name: "lastAlert" , value: "None")
sendEvent(name: "lastAlertType" , value: "None")
sendEvent(name: "armStatus" , value: "Unknown")
sendEvent(name:"statusText", value:"Unknown") 
sendEvent(name: "armDisarm", value: "unlite")
sendEvent(name: "armStay", value: "unlite")
sendEvent(name: "armAway", value: "unlite")

}


def deviceNotification(String desc)
{
log.debug "in device notification"
log.debug "desc = $desc"
def parts = desc.split(":")

if (parts.length == 2)
{

  def command = parts[0]
  def value = parts[1]
  log.debug "command = $command value = *$value*"

switch (command)
{
    case "Status":
    log.debug "thestatus = *$value*"
    sendEvent(name: "armStatus", value: value)
    
    switch (value)
    {
    case " Disarmed":
        sendEvent(name: "armDisarm", value: "lite")
        sendEvent(name: "armStay", value: "unlite")
        sendEvent(name: "armAway", value: "unlite")
        break;

    case " Armed - Away":
        sendEvent(name: "armDisarm", value: "unlite")
        sendEvent(name: "armStay", value: "unlite")
        sendEvent(name: "armAway", value: "lite")
        break;

    case " Armed - Stay":
        sendEvent(name: "armDisarm", value: "unlite")
        sendEvent(name: "armStay", value: "lite")
        sendEvent(name: "armAway", value: "unlite")
        break;
    default: log.debug "in default case value = $value"
    }
    
    sendEvent(name: "status" , value: "All Ok")
    sendEvent(name:"statusText", value: value) 

    case "Alert":
        log.debug "got Alert message value = $value"
        break;

    case "Zones":
      log.debug "Got Zone Message zone = $value"
      sendEvent(name: "lastAlert", value: value)
      break;  

    case "AlertType":
      log.debug "Got alert type Message = *$value*"
	  onPhysical()
      
      switch (value) {
         case " smoke":
          sendEvent(name: "status" , value: "Smoke/CO2 Alert")
          sendEvent(name: "lastAlertType" , value: "Smoke/CO2 Alert")
          break;
      
         case " water":
          sendEvent(name: "status" , value: "Water Alert")
          sendEvent(name: "lastAlertType" , value: "Water Alert")
          break;
     
          default:
          sendEvent(name: "status" , value: "Intrusion Alert")
          sendEvent(name: "lastAlertType" , value: "Intrusion Alert")
          break;
          }
      
      break;
  
      default:
       log.debug "Got unknown Message!"
       }
  
 } // have valid command
	
}



def updated()
{

log.debug "in updated"
sendEvent(name: "status" , value: "All Ok")
sendEvent(name: "lastAlert" , value: "None")
sendEvent(name: "lastAlertType" , value: "None")
sendEvent(name: "armStatus" , value: "Unknown")
sendEvent(name: "statusText", value:"Unknown") 
// for testing onPhysical()
sendEvent(name: "armDisarm", value: "unlite")
sendEvent(name: "armStay", value: "unlite")
sendEvent(name: "armAway", value: "unlite")
}

def on() { 
 	sendEvent(name: "switch", value: "on") 
 } 
 
 
 def off() { 
 	sendEvent(name: "switch", value: "off") 
 } 


def onPhysical() {
	sendEvent(name: "switch", value: "on", type: "physical")
}

def offPhysical() {
	sendEvent(name: "switch", value: "off", type: "physical")
}

