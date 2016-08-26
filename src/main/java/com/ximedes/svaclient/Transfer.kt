package com.ximedes.svaclient

import khttp.get
import khttp.post

data class Transfer(val from : String, val to: String, val amount : Int, val status : String )

fun readTransfer( url : String , expectedStatus : String = "SUCCESS") : Transfer {
    val endpoint = baseUrl.toString() + url
    println("READING account from $endpoint")

    val transferJson = get( endpoint )

    println( if (transferJson.statusCode == 200)
        "-PASS response status is ${transferJson.statusCode} "
    else
        "-FAIL response status was ${transferJson.statusCode}, it should be 200" )


    for ( field in arrayOf( "from" , "to" , "amount" , "status") ) {
        println ( if ( transferJson.jsonObject[ field ] != null )
            "-PASS: Found $field in JSON response "
        else
            "-FAIL: Cannot find $field in JSON response while reading account")
    }
    println( if (transferJson.jsonObject["status"].toString() == expectedStatus )
        "-PASS status is $expectedStatus"
    else
        "-FAIL status was ${transferJson.jsonObject["status"].toString()} but it should be $expectedStatus" )


    return Transfer( transferJson.jsonObject["from"].toString()
            , transferJson.jsonObject["to"].toString()
            , transferJson.jsonObject["amount"].toString().toInt()
            , transferJson.jsonObject["status"].toString()
    )
}


fun transfer( src : String , dest : String, amount : Int , expectedStatus: String = "SUCCESS") : Transfer {
    val endpoint = baseUrl.toString() + "/transfer"
    println( """TRANSFER funds: Sending {"from": "$src" , "to": "$dest", "amount": $amount } to $endpoint""" )

    val response = post( endpoint , data = """{"from": "$src" , "to": "$dest", "amount": $amount }""")
    println( if (response.statusCode == 202 )
        "-PASS : response code is ${response.statusCode.toInt()}"
    else
        "-FAIL : response code is ${response.statusCode.toInt()} , but is should be 202")

    println( if (response.headers["location"] != null )
        "-PASS : response contains a location header:  ${response.headers["location"]}"
    else
        "-FAIL : response does not contain a location header")
    val url = response.headers["location"].orEmpty()
    return readTransfer( url , expectedStatus )
}

