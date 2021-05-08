# BeMyVoice

Make sure you have **Android 10 SDK**

Perform the following steps to set up the project in your android studio:

1) run the command **`git clone https://github.com/faiz276482/BeMyVoice`**
2) Connect the project to firebase either manually and copy the google-services.json file in the app folder of the project/ Use the android studio firebase tool to connect the project.
3) Make sure to enable google authentication in the firebase project.
4) Enable FirestoreDatabse with the following rule:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

5) Enable cloud storage in firebase project with this rule:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

6) Go to the google cloud project console and connect it with the same project you created in step 2
7) search Cloud Text-to-Speech API
8) Enable it
9) Navigate to the credentials tab in the side menu
10) Create new credentials and a file will be generated with a private API key. This file is very important. Download it and keep it on standby.
11) Navigate to this folder in the project BeMyVoice\app\src\main\res
12) Create a new folder called 'raw'
13) copy the credentials.json file here that was downloaded in step 10.
14) Create a new API key
15) Copy it on a text file and keep it on standby.
16) Navigate to this folder in the project BeMyVoice\app\src\main\java\com\nerdytech\bemyvoice\CloudTextToSpeech
17) create a class called Google_API.java
18) paste this inside the class:
```JAVA
package com.nerdytech.bemyvoice.CloudTextToSpeech;

public class Google_API {
    public static final String API_KEY ="PASTE_YOUR_API_KEY_here" ;
}
```
19) replace PASTE_YOUR_API_KEY_here with the API key generated in step 14.
20) clean project
21) rebuild project

You should be good to go. :thumbsup: :smiley:
In case of any problems :no_entry_sign: please create an issue in the GitHub repo itself I will look into it ASAP! :bowtie:

