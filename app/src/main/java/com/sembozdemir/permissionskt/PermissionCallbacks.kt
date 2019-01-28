package com.sembozdemir.permissionskt

interface PermissionCallbacks {

    /**
     * Will be invoked if all permissions are granted.
     */
    fun onGranted()

    /**
     * Will be invoked if any permission is denied.
     *
     * @param permissions list of permissions user denied
     */
    fun onDenied(permissions: List<String>)

    /**
     * Will be invoked if rationale message should be shown.
     *
     * @param permissionRequest use this to retry permission request
     */
    fun onShowRationale(permissionRequest: PermissionRequest)

    /**
     * Will be invoked if any permission is permanently denied.
     *
     * @param permissions list of permissions user select never ask again
     */
    fun onNeverAskAgain(permissions: List<String>)
}