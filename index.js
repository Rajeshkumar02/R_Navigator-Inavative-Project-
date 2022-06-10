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

const auth = firebase.auth()


//Login Function
function login(event) {
    event.preventDefault();
    console.log("clicked");

    var email = document.getElementById("inputEmail")
    var password = document.getElementById("inputPassword")

    auth.signInWithEmailAndPassword(email.value, password.value)
        .then((userCredential) => {
            // location.reload();
            var user = userCredential.user;
            console.log("user", user.email);
            localStorage.setItem("email", user.email);
            window.location = "./HTML/Home.html";
        })
        .catch((error) => {
            var errorCode = error.code;
            var errorMessage = error.message;
            // alert("error code", errorCode)
            alert(errorMessage)
        });
}