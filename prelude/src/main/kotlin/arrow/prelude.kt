package arrow

/**
 * Type Proofs are an implementation of the Curry–Howard–Lambek correspondence over the Kotlin compiler and type system.
 * https://en.wikipedia.org/wiki/Curry%E2%80%93Howard_correspondence#Curry%E2%80%93Howard%E2%80%93Lambek_correspondence
 */

/*yyyyyyyyyyyssssssssssyyyhdddddhhyysyyhhysssssssssssyyyyyyyyyyyo/syyyyyyyyyyyyyyyyyyyyso++++oys+ooo+++//////+yyssyyyyyyyyyyyyyyyyo/syyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyhyyssyyyyyysyyyyyyyyyyyyyyyyy+
myyyyyyssssssooooooooosydNNNmhys+/:-----+shhyssoooooossssssssssy: +sssssssssssssssssso:-+syhhdmms:/:...`````.`-.`./oyyysssssssssss: +sssssssssssssssssssssssyyyssooooossosyddyysssssyyssssssssssssssssys
myyyyyssssooooooooooshNMmhhyo+/-.```    ``.:+sssoooooossssssssss: +ssssssssssssssso/--/++ysyhhyyysso/-.```.ohhy/:/:-:+yhysssssssss: +sssssssssssssssssssyyyyyo+ohdsoooo/:-.:+oyyssyhyssyysssssssssssssys
myyyyssssoooooo+ooshmNmdhhysooss+:.`       ./+osooooooooosssssss: +ssssssssssssss/.:oohdydNhdmhs/-:/o/+:+:-:--...`./s+-:osysssssss: +sssssssssssssssshdmmdhyssyhmmydNMm+:-:hdsssshmmdmddhsosssssssssssys
dyyysssssooooo++oymNmdhdddhhhhyysys:-.`     `:yhyssooooooooossss: +sssssssssssso:/ymmdNmhdmmddyooyhhso+-:--//sh:````.--``:ohysssss: +sssssssssssssshmNNNNMMMmmmmmdhhddddhhsyNNNNNmNNMNNMNdsooyysssssssys
dyssssssooooo++ohNmhydmmdhhyssso//:--::-`     .:+/oysoooooooosss: +ssssssssssso/shmmNmmmNmmddmdyyyhy+/:``/-/ys+oo..`::``-:sohyssss: +ssssssssssssymNMMMMNNNMMMMMMNMMNNmmmNNNNmdhhhyyysyhhmds++ysssssssys
dsssssoooooo+++sNNhyhddhhysso+/:---..`         `:/-oyoooooooosss: +sssssssssss+smmNmhysossso++:--..``   --:/oy+/++::-:..-/oo+hssss: +sssssssssssymNMMNmddddddddddddddddhhddhyssymmNNmmyymmdhyossssssssys
dsssooooooo+++odNhyhdhhyyysso+:-.````            ``-syo+ooooosss: +sssssssssssoshhyo+//:::---..```     `:+:sys++yyo++/-:-:/s+sssss: +ssssssssssydNMNdyyyyyhhhhhhhhhhhhhhhhhhmmdmmdhdddmdmNMmdhysssssssys
dsooooo+++++++sNdyyhhhhhyyssoo/-.````             ``:yo++oooooss: +sssssssssssso+//:::-----...````   ```/dosyyyysshs+:-.-::yyossss: +sssssssssshNMNhyyyyyyyhhhhhhhhhhdddddmmNNMMMMMNNmNmdmNNmhyoosssssys
dooo+++++/////sNhyyhhhhyyysooo/-.``..`            ` .yy+++ooooss: +ssssssssssss++::::-----.....```````..+ddhdddhdyssossss:/+/ossss: +sssssssssshNmhyhyyyhhhhhhhhhddhdddmmmNmmmmNNNMMMNNNmdmNMNmy+ossssys
do++++/////::/sNdhhhdhhyyysoo+:.`````             ` `oy+++++oooo: +ssssssssssss+//::::---......````....-omhssyhmNmds+:-::/hdysssss: +sssssssssodNNdhyyyyhddddddddddddddmmmNmmNNNMMMMMNMMNdmNmmmdy+osssys
h+++////::::::yNdhddhhhhyyssso/:.``               ```ss++++++ooo: +ssssssssssss+/:::::---..````````..../yNsoooydmhyso-.``.-/shysss: +ssssssssssdNNNNmhhdNNNNNNMMMNNmdddddddmmNNMMMMMMMMMNhdNNdyhhs+ossys
h+////:::----:dNddmdhhhdddhyyyso/-..``.````       ``-so//++++ooo: +sssssssssssooo//::/:::---...`` `....:yNssysosmms/:...-+-`/dysss: +sssssssssymmhhdmdmNNNmmmmmmNNNNmddddmmmmmNMMMMMNNMNmdhyhNNdhs+ossys
h///:::----:osdmddddhhddddhhdmmmdhs/-/+::///+//:-``./+////+++ooo: +sssssssssoymNNmho+//+shhddhhy/-.``..-/mmyso+//oyyo/--+dd+:sssss: +sssssssssydyyhdddmmmmmddddddddmdmmmmmNNNmNNMMMMMNMNNNmhhMMNNmsossys
h/::::---.-sMMddmddhyydmdmmNmmdmmNNh/::smmmmhyo/...-+/:////++ooo: +sssssssysydNNNNNmhsshmNNmNNNNmdhysyyhdmNNmdhdhsosyys//+yyosssss: +ssssssssssysyddddmmmNdhyhdhhhhdddmmmNNMNNmNmmmNMMMNmmdmNMNmddhsssys
y:::---...-yMmdmmddhyssyhhhdmhyhhdNm:``hMNNNmdhs/..sd+::///+++oo: +sssssssds+sshmmdNNmNhydmmdNmNNmNmdyo+//sNNmmmmmddhdNyoo/+ssssss: +ssssssssssydyydmmmmmNMmhyyhddddmdddmNMMNmmmddddmmNmmNNMNNmmmddyssys
y::---...`.sMddNNmddysooooo++osssyNN:  -+syy+//+:`-mh/:::///+++o- +ssssssss+```/hhhms:ymoohhyyyhmooo/-....:smmdhhmNmsoddhs/ossssss: +ssssssssssshdhdmmmdddmNNdhhdddmmmddddmmddddddddddmNMNNNNdsyhhysssys
y---....```:ddhmNmddhyso+/:::/+sydMm-    -o+-`    .s/:::::///+++- +sssssssso-..+yydh:.-yds++++oyh:...`....-odmdo/oyNhohNyossssssss: +sssssssssssyddhdmmNNNNNNNNdhddddmddddmdddddmdmNMMMMMMNNNd++osssssys
y--...```` `/mhdNmddhhyso+:--:+ymmNy.  `          -+----::////++- +sssssssssso+yyyho-.-:yhyysoso:-.....--:/ohhy+:::oodNdddhyssssss: +sssssssssssymmhhdmmmddddmmhhdddddddddddddmmmmNmddmmdyshmNdhyyssssys
s...`````   .dmhNNmdhhhyys+::/odmdmh/....`       `:-----:::///++- +sssssssssssssyys/-.--/+o+//:-----:::://+oo++o//:omNmdNMMmhyssss: +ssssssssssshdssydmmmmmddddddddddddddddmNNNmdyo++ssoo-  .:oyyhdddhhs
s..````      .ohNNmdhhhyyys+++sdNNMMNddhoy+.   ``.-...---:::////- +ssssssssssssshdo:--/+ooyyso++////++o+/:::+/++/odNNmmNMMMMMNdyss: +ssssssssssymmhooosyyhhhhdmmmddddmdmmNNNmdyo+//++sso/-`  `   ``-:/ss
o``````        -NMmddhhyyyysooosyyhmdo/-`:hs:````......---:::///. +sssssssssssssshhyhdNNmho++oossssosho+s:-/+:+hdNMNddNMMNNNNNMmys: +ssssyyyyhhhyoohmddhhdmddhhdmmmmmmmNNmys++///+oshds-``   `        .s
o````          .mMmdddhhyysssssssssss/.`` ./o:..-```....---::://. +sssssssssssssssdmmNmmddyoso++ooysyoo++:--:/+/+dMNNNNMMMMMNNNMmy: +syhhso+:-.``+dNNNmdmNMMNdhhhdmNmmddmh++++++oydmho:`    `...-.``/.-s
o```           .mMNmddhhyysshdmmmmmddddysso---.:-````....---::::. +sssssssssssssoomNNNmmNmmdmdhys//+sooo+:/+://:-oNMMMMMMMMMMMNNMN/ sdho+yhy/:``sNNddmmdmNMNmmddddmNdyhmNmoooosyddhs/-``.../ydhhds/ymhhs
o`            .sMNmmmmdhyysyyhhdmddhhys+/+ss:.--```````...----::. +ssssssssssssyhyymNdddmdmmNNNNh+/oyysso:/o/::..+NMMMMMMMMMMMMMMMs so/osms//+./mmdddddddNNmmmmmmmmNmyhmNysyhddhyyyo--+/+:+hhssssosyhyhs
+           .omMNmdmNNmdhhyyyssyyhdmmmh/.``-++/`   ````....---::. +ssssssssssyhNNyyddhyhmddmdhhdddyhyymhdyosoo/..+NMNMMMMMMMMMMMNNs :/syyy+/::++ohmmddhyhNMNmmmmmNMNmhdNmyyddhssshy//ys//+ysoooossysssys
+       `.:odMNMNddmmNNmdhhyyysso+/:::-.....:++-.```````....---:. +ssssssssyhNMMdshdmhhdmdddhhdddmhyhydmmmhhsss::sNMNMMMMMMMMMMMMMs :+hhs+//:/so//oysooosshmNNNNNmdddhyyhhyysssyhs/+hy+/oysoooossyyssshs
+   ``-+hmMNNmmNMdydNNNNNmddhhyys+////-...::yNMNmhhyo+:---...---` +ssssssshNMMMNhymmmdddhhdddmmmmNmdmdhmNmhhy+o+sNMMNMMMMMMMMMMMMMo /sys+//::+o+/////++osssshNMmhhhyysssssssssyyo+oyyo++sssoosssyyysyyhs
o.-+ymNMNNmmmmmmNMdoymNNNNNNmmdhhdddmdhyshh:hMNmmmmNNNNNmhyo/:--` +sssssymMMMMMNdhmdmmmhdmNNNNNNNNmmNmmmmddhdssmmMNNMMMMMMMMMMMMMMs +yo++++/:/+//++++ooossyysymhyyyyssssssyssssoosysooossossssssyyyyyyhs
ddNMMNmmmmmmmmmmNNMm+/smNNmmmmmmmmmmmNNNNh-`sMNmddddddmmmNNMNmds- +ssshmMMMMMMMMmdmmNNNmmNNNNMNNNNNmNmmmddhhdddNMNNNNMMNMMMMMMMMMMs +o++oso//////++++++oooshsohdyyssssssyyssoooosssoossssssssssyyyyyyhhs
NNmmmmmmmmmmmmmmmNMMm+--+hNNmdddddddmmhho.  yMdmmddddddddmmmmmNNs +shmMMMMMMMMMMMmdmmNNNNNNNNNNNNMNNNmmNmdhydmMMMMMMMMMMMMMMMMMMMMs /++sss+///////+++osssossoodmyyysssssssooosssssssssssssssssyyyyyyhhhs
NmmmmmmmmmmmmmmmmmNNMmo:..:smNNmdhhdmdh+`  `hNhhdddddddddddddddd+ yNMMNMMMMMMMMMMNmNmNMNNNNNMNNMNNNNNmNNdhddmNMNNMNNNMMMMMMMMMMMMMs /+oyso+//++//++o+ooosyssoodmhysssssssssssssssssssssssssssyyyyyyhyyds
NmmmmmmmmmmmmmmmmmmNNMNo:-...:ohmNmNNs-    .mNyyhddddddddddddddd+`mNNNNNMMMMMMMMMMNNNNNNMNNMMNNMNNNNNNNdhyhmNNMNMNMNMMMMMMMMMMMMMMs /+oysoo+++++++ooosssyyoo+yNhysssyssssssssssssssssssssssyyyyhyyhhhhds
dhhhhhhhhhhhhhhhhhhhhdddsoo+++/++shdy//////oddyyyhhhhhhhhhhhhhhhs/hdddddddddddddddddddddddddddddddddddhhyyhddddddddddddddddddddddds/ossyysyhssyssssyyyyyyyysshdyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyhhyh*/