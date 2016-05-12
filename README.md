An Automated Attendance System using Raspberry Pi for Android Devices.

The student have to do initial registration using Android Application and his attendance is taken care by the system without
his intervention. The Android application includes a Service, is invoked by a Push Notification, shares student information to
the Raspberry Pi via Bluetooth and exits silently.

BLE scripts are run on the Raspberry Pi which open the rfcomm port and keep listening to the incoming requests and stores the
information of the student.
