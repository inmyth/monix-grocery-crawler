var ownerId = 10092

var a =  `


Pet Republic Jakarta
4.6

Directions
Save
Nearby
Send to your phone
Share
Jl. Pluit Karang Bar., RT.8/RW.2, Pluit, Penjaringan, Kota Jkt Utara, Daerah Khusus Ibukota Jakarta 14450, Indonesia
VQQF+GP Jakarta, Indonesia
petrepublicjakarta.com
+62 21 22663352
Open now
Friday


    9AM–9PM


Saturday


    9AM–9PM


Sunday


    9AM–9PM


Monday


    9AM–9PM


Tuesday


    9AM–9PM


Wednesday


    9AM–9PM


Thursday


    9AM–9PM


Add a label
`

var g = `
https://www.google.com/maps/place/Pet+Republic+Jakarta/@-6.1112262,106.774259,15z/data=!4m5!3m4!1s0x0:0xd69e67bef9b4735d!8m2!3d-6.1112262!4d106.774259

`

var b = a.split(/[\r\n]+/)
var c = b.filter(p => p != "").map(p => p.trim())

var name = c[0]

var address = c[c.indexOf("Share")+1]
if (address.endsWith(", Indonesia")){
    address = address.substring(0, address.length-11)
}
var address = address.trim()

var re = /\b[0-9]{5}\b/

var zip = address.match(re)[0]

//var hours = b.filter(p => p.includes("Monday"))[0].substring(6).trim().split("–").map(p => p.slice(0, -2))
var hours = c[c.indexOf("Monday")+1].split("–").map(p => p.slice(0, -2))
var open  = hours[0].length == 1 ? "0" + hours[0] + ":00" : hours[0] + ":00"
var close = parseInt(hours[1]) + 12 + ":00"
var phone = b.filter(p => p.startsWith("+"))[0]

var point = "point(" + g.substring(g.indexOf("!4d") + 3).trim() + ","  + g.substring(g.indexOf("!3d")+ 3, g.indexOf("!3d")+13)  + ")"

// open = '24H'
// close = ''

console.log("INSERT INTO store ( name, geo, ownerId, currency, zip, address, city, phone, open, close, createTs) VALUES" +
"(\n" +
"'" + name  + "',\n"+
point + ",\n" +
ownerId + ",\n" +
"'IDR',\n" +
"'" + zip + "',\n" +
"'" + address + "',\n" +
"'Jakarta',\n" +
"'" + phone + "',\n" +
"'" + open + "',\n" +
"'" + close + "',\n" +
"1553661076" +
"\n)")


/*
(
'Transmart Carrefour',
point (115.1868972,-8.7129708),
10051,
'IDR',
'80221',
'Sunset Road Building Lt. 3, Jl. Sunset Road, Pemogan, Denpasar Selatan, Kuta, Kabupaten Badung, Bali 80221' ,
'Denpasar',
'+62 822-0825-5125',
'08:00',
'22:00',
1553661076
)

console.log(name)
console.log(point)
console.log(zip)
console.log(address)
console.log(open)
console.log(close)
console.log(phone)
*/