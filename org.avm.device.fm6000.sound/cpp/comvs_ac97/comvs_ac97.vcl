<html>
<body>
<pre>
<h1>Build Log</h1>
<h3>
--------------------Configuration: comvs_ac97 - Win32 (WCE ARMV4I) Release--------------------
</h3>
<h3>Command Lines</h3>
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP2082.tmp" with contents
[
/nologo /W3 /Oxt /I "C:\Program Files\Java\jdk1.6.0_04\include\win32" /I "C:\Program Files\Java\jdk1.6.0_04\include" /I "C:\opt\comvs\include" /D _WIN32_WCE=420 /D "ARM" /D "_ARM_" /D "WCE_PLATFORM_STANDARDSDK" /D "ARMV4I" /D UNDER_CE=420 /D "UNICODE" /D "_UNICODE" /D "NDEBUG" /D "comvs_ac97_EXPORTS" /Fo"ARMV4IRel/" /QRarch4T /QRinterwork-return /MC /c 
"C:\Users\dsu\affaires\geolia\avm\device\fm6000\org.avm.device.fm6000.sound\cpp\comvs_ac97\comvs_ac97_wrap.c"
]
Creating command line "clarm.exe @C:\Users\dsu\AppData\Local\Temp\RSP2082.tmp" 
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP2092.tmp" with contents
[
commctrl.lib coredll.lib LIB_AC97.lib /nologo /base:"0x00100000" /stack:0x10000,0x1000 /entry:"_DllMainCRTStartup" /dll /incremental:no /pdb:"ARMV4IRel/comvs_ac97.pdb" /nodefaultlib:"libc.lib /nodefaultlib:libcd.lib /nodefaultlib:libcmt.lib /nodefaultlib:libcmtd.lib /nodefaultlib:msvcrt.lib /nodefaultlib:msvcrtd.lib" /out:"ARMV4IRel/comvs_ac97.dll" /implib:"ARMV4IRel/comvs_ac97.lib" /libpath:"C:\opt\comvs\lib" /subsystem:windowsce,4.20 /MACHINE:THUMB 
.\ARMV4IRel\comvs_ac97_wrap.obj
]
Creating command line "link.exe @C:\Users\dsu\AppData\Local\Temp\RSP2092.tmp"
<h3>Output Window</h3>
Compiling...
comvs_ac97_wrap.c
Linking...
   Creating library ARMV4IRel/comvs_ac97.lib and object ARMV4IRel/comvs_ac97.exp
Creating temporary file "C:\Users\dsu\AppData\Local\Temp\RSP2499.bat" with contents
[
@echo off
xcopy ARMV4IRel\comvs_ac97.dll ..\..\lib /Y
]
Creating command line "C:\Users\dsu\AppData\Local\Temp\RSP2499.bat"

ARMV4IRel\comvs_ac97.dll
1 fichier(s) copi�(s)




<h3>Results</h3>
comvs_ac97.dll - 0 error(s), 0 warning(s)
</pre>
</body>
</html>
