
definition(
    name:"Gideon",
    namespace:"gideon.api",
    author:"Braindrain Solutions",
    description:"Gideon AI Smart app allows you to connect and control all of your SmartThings devices through the Gideon AI app,making your SmartThings devices even smarter.",
    category:"Family",
    iconUrl:"http://s33.postimg.org/t77u7y7v3/logo.png",
    iconX2Url:"http://s33.postimg.org/t77u7y7v3/logo.png",
    iconX3Url:"http://s33.postimg.org/t77u7y7v3/logo.png",
    oauth:[displayName:"Gideon AI API",displayLink:"gideon.ai"])


preferences{
    section("Control these switches..."){
        input"switches","capability.switch",multiple:true
        }
    section("Control these motion sensors..."){
        input"motions","capability.motionSensor",multiple:true
        }
    section("Control these presence sensors..."){
        input"presence_sensors","capability.presenceSensor",multiple:true
        }
    section("Control these outlets..."){
        input"outlets","capability.switch",multiple:true
        }
    section("Control these locks..."){
        input"locks","capability.lock",multiple:true
        }
    section("Control these locks..."){
        input"temperature_sensors","capability.temperatureMeasurement"
        }
}

def installed(){
    def phone="1111111111"
    def msg="Hello"
    msg="Hello"
    msg="${switches}"
    sendSms(msg,phone)
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){
    
    subscribe(outlet,"energy",outletHandler)
    subscribe(outlet,"switch",outletHandler)
}


def outletHandler(evt){
    log.debug"$outlet.currentEnergy"

}


private device(it,type){
    it?[id:it.id,label:it.label,type:type]:null
}


mappings{
    path("/getalldevices"){
        action:[
        GET:"getAllDevices"
        ]
        }
    path("/doorlocks/:id/:command"){
        action:[
        GET:"updateDoorLock"
        ]
        }
    path("/doorlocks/:id"){
        action:[
        GET:"getDoorLockStatus"
        ]
        }
    path("/tempsensors/:id"){
        action:[
        GET:"getTempSensorsStatus"
        ]
        }
    path("/presences/:id"){
        action:[
        GET:"getPresenceStatus"
        ]
        }
    path("/motions/:id"){
        action:[
        GET:"getMotionStatus"
        ]
        }
    path("/outlets/:id"){
        action:[
        GET:"getOutletStatus"
        ]
        }
    path("/outlets/:id/:command"){
        action:[
        GET:"updateOutlet"
        ]
        }
    path("/switches/:command"){
        action:[
        PUT:"updateSwitch"
        ]
        }
}


def getAllDevices(){
    def locks_list=locks.collect{device(it,"Lock")}
    def presences_list=presence_sensors.collect{device(it,"Presence")}
    def motions_list=motions.collect{device(it,"Motion")}
    def outlets_list=outlets.collect{device(it,"Outlet")}
    def switches_list=switches.collect{device(it,"Switch")}
    def temp_list=temperature_sensors.collect{device(it,"Temperature")}
    return[Locks:locks_list,Presences:presences_list,Motions:motions_list,Outlets:outlets_list,Switches:switches_list,Temperatures:temp_list]
}


def getDoorLockStatus(){
    def device=locks.find{it.id==params.id}
    if(!device){
        httpError(404,"Device not found")
    }else{
            return[Device_state:device.currentValue('lock')]
        }
}

def updateDoorLock(){
    def command=params.command
    def device=locks.find{it.id==params.id}
    if(command){
        if(!device){
            httpError(404,"Device not found")
        }else{
                if(command=="toggle")
                {
                    if(device.currentValue('lock')=="locked")
                    device.unlock();
                    else
                    device.lock();
                    
                    return[Device_id:params.id,result_action:"200"]
                }
            }
    }
}


def getPresenceStatus(){
    
    def device=presence_sensors.find{it.id==params.id}
    if(!device){
        httpError(404,"Device not found")
    }else{
            return[Device_state:device.currentValue('presence')]
        }
}


def getMotionStatus(){
    
    def device=motions.find{it.id==params.id}
    if(!device){
        httpError(404,"Device not found")
    }else{
            return[Device_state:device.currentValue('motion')]
        }
}


def getOutletStatus(){
    
    def device=outlets.find{it.id==params.id}
    if(!device){
        httpError(404,"Device not found")
    }else{
            return[Device_state:device.currentSwitch,Current_watt:device.currentValue("energy")]
        }
}

def updateOutlet(){
    
    def command=params.command
    def device=outlets.find{it.id==params.id}
    if(command){
        if(!device){
            httpError(404,"Device not found")
        }else{
                if(command=="toggle")
                {
                    if(device.currentSwitch=="on")
                    device.off();
                    else
                    device.on();
                    
                    return[Device_id:params.id,result_action:"200"]
                }
            }
    }
}


def updateSwitch(){
    def command=params.command
    def device=switches.find{it.id==params.id}
    if(command){
        if(!device){
            httpError(404,"Device not found")
        }else{
                if(command=="toggle")
                {
                    if(device.currentSwitch=="on")
                    device.off();
                    else
                    device.on();
                    
                    return[Device_id:params.id,result_action:"200"]
                }
            }
    }
}


def getTempSensorsStatus(){
    
    def device=temperature_sensors.find{it.id==params.id}
    if(!device){
        httpError(404,"Device not found")
    }else{
            return[Device_state:device.currentValue('temperature')]
        }
}

