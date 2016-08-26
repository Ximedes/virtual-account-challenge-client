package com.ximedes.svaclient

import java.net.URL

var baseUrl : URL = URL( "http://localhost:8080" )

fun main( args : Array<String>) {
    baseUrl = URL( if (args.isEmpty() ) "http://localhost:8080" else args[0])

    val account1 =  createAccount( 1000 )
    val account2 =  createAccount( 0 )

    transfer( account1.accountId , account2.accountId , 1000 , expectedStatus = "SUCCESS")


    if ( readAccount( "/account/" + account1.accountId , silent = true ).balance == -1000 &&  readAccount( "/account/" + account2.accountId , silent = true ).balance == 1000 ) {
        println( "-PASS : After transfer account ${account1.accountId} is -1000 and account ${account1.accountId} is 1000" )

    } else {
        println( "-FAIL : After transfer account ${account1.accountId} is NOT -1000 or account ${account1.accountId} is NOT 1000" )
    }

    transfer( account2.accountId , account1.accountId , 1001 , expectedStatus = "INSUFFICIENT_FUNDS")

    if ( readAccount( "/account/" + account1.accountId , silent = true ).balance == -1000 &&  readAccount( "/account/" + account2.accountId , silent = true ).balance == 1000 ) {
        println( "-PASS : After transfer account ${account1.accountId} is -1000 and account ${account1.accountId} is 1000" )

    } else {
        println( "-FAIL : After transfer account ${account1.accountId} is NOT -1000 or account ${account1.accountId} is NOT 1000" )
    }



}

