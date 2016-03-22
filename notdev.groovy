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
   
  }

  simulator {
  }

  tiles(scale: 2) {
    multiAttributeTile(name:"status",type: "generic", width: 6, height: 4){
      tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
        attributeState "All Ok", label:'${name}', backgroundColor:"#4f9558"
        attributeState "Water Alert", label:'${name}', backgroundColor:"#007f8f"
        attributeState "Smoke/CO2 Alert", label:'${name}', backgroundColor:"#711100"
        attributeState "Intrusion Alert", label:'${name}', backgroundColor:"#8a0707"
      }
      
       tileAttribute ("statusText", key: "SECONDARY_CONTROL") { 
         attributeState "statusText", label:'${currentValue}'       		 
      } 

	}
    
    
    valueTile("armStatus", "device.armStatus", inactiveLabel: false, width: 6, height: 2) {
			state "default", label:'Arming Status: ${currentValue}',icon:""
	}		
    valueTile("lastAlert", "device.lastAlert", inactiveLabel: false, width: 6, height: 2) {
			state "default", label:'Last Alert: ${currentValue}',icon:""
            }
    valueTile("lastAlertType", "device.lastAlertType", inactiveLabel: false, width: 6, height: 2) {
			state "default", label:'Last Type: ${currentValue}',icon:""
		
    }
 
}
    main (["status","armStatus","lastAlert","lastAlertType"])
    details(["status","armStatus","lastAlert","lastAlertType"])
}


def installed() {
log.debug "in installed"
sendEvent(name: "status" , value: "All Ok")
sendEvent(name: "lastAlert" , value: "None")
sendEvent(name: "lastAlertType" , value: "None")
sendEvent(name: "armStatus" , value: "Unknown")
sendEvent(name:"statusText", value:"Unknown") 

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
  log.debug "command = $command value = $value"

switch (command)
{
    case "Status":
    log.debug "thestatus = $value"
    sendEvent(name: "armStatus", value: value)
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
      log.debug "Got alert type Message zone = $value"

      if (value == "smoke")
      {
      sendEvent(name: "status" , value: "Smoke/CO2 Alert")
      sendEvent(name: "lastAlertType" , value: "Smoke/CO2 Alert")
      }
      else if (value == "water")
      {
      sendEvent(name: "status" , value: "Water Alert")
      sendEvent(name: "lastAlertType" , value: "Water Alert")
      }
      else
      {
      sendEvent(name: "status" , value: "Intrusion Alert")
      sendEvent(name: "lastAlertType" , value: "Intrusion Alert")
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
sendEvent(name:"statusText", value:"Unknown") 

}


