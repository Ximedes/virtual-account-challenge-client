package com.ximedes.svaclient

import khttp.get
import khttp.post
import kotlin.system.exitProcess

data class Account(val accountId : String, val balance : Int, val overdraft : Int = 0  )


fun readAccount( url : String , silent : Boolean = false ) : Account {
    val endpoint = baseUrl.toString() + url
    println("READING account from $endpoint")

    val accountJson = get( endpoint )
    if (!silent) println( if (accountJson.statusCode == 200)
        "-PASS response status is ${accountJson.statusCode} "
    else
        "-FAIL response status was ${accountJson.statusCode}, it should be 200" )

    val accountId : String = accountJson.jsonObject["accountId"].toString()
    val balance   = accountJson.jsonObject["balance"].toString().toInt()
    val overdraft = accountJson.jsonObject["overdraft"].toString().toInt()

    if (!silent)
        for ( field in arrayOf( "accountId" , "balance" , "overdraft") ) {
            println ( if ( accountJson.jsonObject[ field ] != null )
                "-PASS: Found $field in JSON response "
            else
                "-FAIL: Cannot find $field in JSON response while reading account")
        }
    return Account( accountId , balance, overdraft )
}

fun createAccount( overdraft : Int ) : Account {
    val endpoint = baseUrl.toString() + "/account"

    println("""CREATING account. Sending {"overdraft": $overdraft } to endpoint $endpoint""")

    val response = post( endpoint , data = """{"overdraft": $overdraft }""")
    if (response.statusCode == 202) {
        println("-PASS : statuscode was ${response.statusCode}")
        val url = response.headers["location"].orEmpty()
        if (url.isEmpty()) {
            println("-FAIL : location header was not present in response, or it was empty")
        } else {
            println("""-PASS : location header contains ${response.headers["location"]}""" )
        }
        return readAccount( url )
    } else {
        println("-FAIL : statuscode was ${response.statusCode} , but 202 was expected")
        exitProcess(1)
        return Account("ERROR", 0, 0)
    }


}
