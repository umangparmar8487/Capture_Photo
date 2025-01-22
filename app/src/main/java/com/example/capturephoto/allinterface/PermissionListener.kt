package com.example.capturephoto.allinterface

interface PermissionListener {
    fun permissionGranted(permission: String)
    fun permissionDenied(permission: String)
    fun permissionForeverDenied(permission: String)
}
