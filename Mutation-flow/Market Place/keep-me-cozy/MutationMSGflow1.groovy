
definition(
    name:"Keep Me Cozy",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Changes your thermostat settings automatically in response to a mode change.Often used with Bon Voyage,Rise and Shine,and other Mode Magic SmartApps to automatically keep you comfortable while you're present and save you energy and money while you are away.",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2 x.png"
)

preferences{
    section("Choose thermostat..."){
        input"thermostat","capability.thermostat"
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Heat setting..."){
        input"heatingSetpoint","number",title:"Degrees?"
        }
    section("Air conditioning setting..."){
        input"coolingSetpoint","number",title:"Degrees?"
        }
}

def installed()
{
    subscribe(thermostat,"heatingSetpoint",heatingSetpointHandler)
    subscribe(thermostat,"coolingSetpoint",coolingSetpointHandler)
    subscribe(thermostat,"temperature",temperatureHandler)
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def updated()
{
    def message="${thermostat}"
    message="Hello"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(thermostat,"heatingSetpoint",heatingSetpointHandler)
    subscribe(thermostat,"coolingSetpoint",coolingSetpointHandler)
    subscribe(thermostat,"temperature",temperatureHandler)
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def heatingSetpointHandler(evt)
{
    log.debug"heatingSetpoint:$evt,$settings"
}

def coolingSetpointHandler(evt)
{
    log.debug"coolingSetpoint:$evt,$settings"
}

def temperatureHandler(evt)
{
    log.debug"currentTemperature:$evt,$settings"
}

def changedLocationMode(evt)
{
    log.debug"changedLocationMode:$evt,$settings"
    
    thermostat.setHeatingSetpoint(heatingSetpoint)
    thermostat.setCoolingSetpoint(coolingSetpoint)
    thermostat.poll()
}

def appTouch(evt)
{
    log.debug"appTouch:$evt,$settings"
    
    thermostat.setHeatingSetpoint(heatingSetpoint)
    thermostat.setCoolingSetpoint(coolingSetpoint)
    thermostat.poll()
}


def event(evt)
{
    log.debug"value:$evt.value,event:$evt,settings:$settings,handlerName:${evt.handlerName}"
}

