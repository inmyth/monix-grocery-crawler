var a =  `
Jakarta Fruit Market
4.31,257 Google reviews
Fruit and vegetable store
Address: Jl. Pluit Putra Raya No. No.10, RT.2/RW.6, Pluit, Jakarta Capital Region, Kota Jkt Utara, Jakarta Capital Region 14450, Indonesia
Hours:
Friday	7:30AM–11PM
Saturday	7:30AM–11PM
Sunday	7:30AM–11PM
Monday	7:30AM–11PM
Tuesday	7:30AM–11PM
Wednesday	7:30AM–11PM
Thursday	7:30AM–11PM
Suggest an edit


Phone: +62 21 6693805
`

var g = `

https://www.google.com/maps/place/Jakarta+Fruit+Market/@-6.1219857,106.79146,15z/data=!4m5!3m4!1s0x0:0xb3e8f18f08581a7e!8m2!3d-6.1219857!4d106.79146
`

var b = a.split(/[\r\n]+/)
var c = b.filter(p => p != "")

var name = c[0]
var address = b.filter(p => p.includes("Address:"))[0].substring(8)
if (address.endsWith(", Indonesia")){
    address = address.substring(0, address.length-11)
}

var address = address.trim()
var re = /\b[0-9]{5}\b/

var zip = address.match(re)[0]

var hours = b.filter(p => p.includes("Monday"))[0].substring(6).trim().split("–").map(p => p.slice(0, -2))
var open  = hours[0].length == 1 ? "0" + hours[0] + ":00" : hours[0] + ":00"
var close = parseInt(hours[1]) + 12 + ":00"
var phone = b.filter(p => p.includes("Phone:"))[0].substring(6).trim()

var point = "point(" + g.substring(g.indexOf("!4d") + 3).trim() + ","  + g.substring(g.indexOf("!3d")+ 3, g.indexOf("!3d")+13)  + ")"

// open = '24H'
// close = ''

console.log("INSERT INTO store ( name, geo, ownerId, currency, zip, address, city, phone, open, close, createTs) VALUES" +
"(\n" +
"'" + name  + "',\n"+
point + ",\n" +
"10053,\n" +
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