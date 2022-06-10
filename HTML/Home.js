const firebaseConfig = {
    apiKey: "AIzaSyDGFCMt9CinGKYNKW63Km2hOUSY8vnKY-g",
    authDomain: "inavative-project.firebaseapp.com",
    databaseURL: "https://inavative-project-default-rtdb.firebaseio.com",
    projectId: "inavative-project",
    storageBucket: "inavative-project.appspot.com",
    messagingSenderId: "846062743189",
    appId: "1:846062743189:web:bdf8c0f00fcd1f2417b59c",
    measurementId: "G-WZ2WF424XJ"
};

firebase.initializeApp(firebaseConfig);

const db = firebase.firestore();

const auth = firebase.auth()

const name = localStorage.getItem("email").split("@");

let arr = [];

let locations = new Map();

window.onload = db.collection(name[0])
    .get()
    .then(snap => {
        snap.forEach(doc => {
            // console.log(doc.data());
            // console.log(doc.id);
            if (doc.id === "Last") {
                arr = [...arr, doc.data()];
                // console.log(arr[0].Date);
            }
            if (doc.id !== "Last" && doc.id !== "Created_On") {
                locations.set(doc.id, doc.data());
                // console.log(locations);
            }
        });
        getval();
        console.log(arr)
    });

// locations.forEach((key, value) => {
//     console.log(value + ":-");
//     for (const [xkey, xvalue] of Object.entries(key)) {
//         console.log(`${xkey} ==> ${xvalue}`);
//     }
// }
// );

function signout() {
    auth.signOut();
    window.location = "../index.html";
}


function getval() {

    document.getElementById("lname").innerHTML = "<h3>Welcome back !  " + name[0] + "</h3>"

    document.getElementById("current").innerHTML = "<h2><b>Last Location</b></h2><br/><b>Date :-</b>" + arr[0].Date + "<br/><b>Time :-</b>" + arr[0].Time + "<br/><b>Longitude :-</b>" + arr[0].Longitude + "<br/><b>Latitude :-</b>" + arr[0].Latitude + "<br/>";

    locations.forEach((key, value) => {

        var text = value;
        var newParagraph = document.createElement("p");
        var newText = document.createTextNode(text);
        newParagraph.appendChild(newText);
        document.getElementById("val").appendChild(newParagraph);

        // console.log(value + ":-");
        for (const [xkey, xvalue] of Object.entries(key)) {

            var text = xkey;
            var newParagraph = document.createElement("p");
            var newText = document.createTextNode(text);
            newParagraph.appendChild(newText);

            var a = document.createElement('a');
            var link = document.createTextNode(xvalue);
            a.appendChild(link);
            a.title = xvalue;
            a.href = "https://www.google.com/maps/search/?api=1&query=" + xvalue;
            document.getElementById("val").appendChild(newParagraph);

            document.getElementById("val").appendChild(a);

            // console.log(`${xkey} ==> ${xvalue}`);
        }
    }
    );
}



// function getval() {
//     locations.forEach((key, value) => {
//         var child = document.createElement("li");
//         outside = value + " :-"
//         var demo = document.createTextNode(outside);
//         child.appendChild(demo);
//         document.getElementById("insertval").appendChild(child);

//         // //br tag
//         // var br = document.createElement("br");
//         // document.getElementById("insertval").append(br);

//         console.log(value + ":-");
//         for (const [xkey, xvalue] of Object.entries(key)) {
//             var child = document.createElement("ol");
//             var loc = "https://www.google.com/maps/search/?api=1&query=" + xvalue;

//             var inside = xkey + " ==> ";
//             var demo = document.createTextNode(inside);
//             child.appendChild(demo);
//             document.getElementById("insertval").appendChild(child);


//             var mydiv = document.getElementById("insertval");
//             var aTag = document.createElement('a');
//             aTag.setAttribute('href', loc);
//             aTag.innerText = xvalue;
//             mydiv.appendChild(aTag);

//             console.log(`${xkey} ==> ${xvalue}`);
//         }
//         //br tag
//         var br = document.createElement("br");
//         document.getElementById("insertval").append(br);
//     }
//     )
// }