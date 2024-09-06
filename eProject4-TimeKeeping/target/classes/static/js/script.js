// const container = document.querySelector('#container');
// const video = document.querySelector('#video');
// const canvasContainer = document.querySelector('#canvas-container');
// const startCameraButton = document.querySelector('#start-camera');
// const infoDiv = document.querySelector('#info');
// const notificationDiv = document.querySelector('#notification');
//
//
// let faceMatcher;
// const personDetails = {
//     'Fukada Eimi': { age: 25 },
//     'Rina Ishihara': { age: 30 },
//     'Takizawa Laura': { age: 28 },
//     'Yua Mikami': { age: 27 },
//     'HoanTrong': { age: 19 }
// };
//
// async function loadTrainingData() {
//     const labels = Object.keys(personDetails);
//     const faceDescriptors = [];
//
//     for (const label of labels) {
//         const descriptors = [];
//         for (let i = 1; i <= 6; i++) {
//             try {
//                 const imageUrl = `/data/${label}/${i}.jpeg`;
//                 const image = await faceapi.fetchImage(imageUrl);
//                 await faceapi.nets.tinyFaceDetector.loadFromUri('/models');
//
//                 const detection = await faceapi.detectSingleFace(image, new faceapi.TinyFaceDetectorOptions()).withFaceLandmarks().withFaceDescriptor();
//
//                 if (detection && detection.descriptor) {
//                     descriptors.push(detection.descriptor);
//                 } else {
//                     console.warn(`No face detected in image: ${imageUrl}`);
//                 }
//
//             } catch (error) {
//                 console.error(`Error loading image for ${label}:`, error);
//             }
//         }
//         if (descriptors.length > 0) {
//             faceDescriptors.push(new faceapi.LabeledFaceDescriptors(label, descriptors));
//         } else {
//             console.warn(`No valid descriptors found for ${label}.`);
//         }
//     }
//     return faceDescriptors;
// }
//
// async function init() {
//     try {
//         // Load face-api.js models
//         console.log("Loading models...");
//         await Promise.all([
//             faceapi.nets.ssdMobilenetv1.loadFromUri('/models')
//                 .catch(err => console.error('Error loading ssdMobilenetv1:', err)),
//             faceapi.nets.faceLandmark68Net.loadFromUri('/models')
//                 .catch(err => console.error('Error loading faceLandmark68Net:', err)),
//             faceapi.nets.faceRecognitionNet.loadFromUri('/models')
//                 .catch(err => console.error('Error loading faceRecognitionNet:', err))
//         ]);
//
//         console.log("Models loaded successfully");
//
//         // Load training data for face recognition
//         const trainingData = await loadTrainingData();
//         console.log("Training data loaded:", trainingData);
//
//         if (trainingData.length > 0) {
//             // Initialize FaceMatcher with the training data
//             const faceMatcher = new faceapi.FaceMatcher(trainingData, 0.7);
//             console.log("FaceMatcher initialized successfully");
//
//             // Start the video stream for face recognition
//             startVideo(faceMatcher);
//         } else {
//             console.error("No valid training data available. Unable to initialize face recognition.");
//             alert("Face recognition could not be initialized due to missing training data.");
//         }
//     } catch (error) {
//         console.error("Error initializing face recognition:", error);
//         alert("Failed to initialize face recognition. Please check the console for details.");
//     }
// }
//
// function startVideo() {
//     navigator.mediaDevices.getUserMedia({ video: true })
//         .then(stream => {
//             video.srcObject = stream;
//             video.onloadedmetadata = () => {
//                 video.play();
//                 detectFaces();
//             };
//         })
//         .catch(err => {
//             console.error("Error accessing webcam: ", err);
//             alert("Unable to access the camera. Please check your permissions.");
//         });
// }
//
// async function detectFaces() {
//     const canvas = faceapi.createCanvasFromMedia(video);
//     canvasContainer.innerHTML = '';
//     canvasContainer.append(canvas);
//
//     const displaySize = { width: video.width, height: video.height };
//     faceapi.matchDimensions(canvas, displaySize);
//
//     setInterval(async () => {
//         const detections = await faceapi.detectAllFaces(video).withFaceLandmarks().withFaceDescriptors();
//         const resizedDetections = faceapi.resizeResults(detections, displaySize);
//
//         canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height);
//         faceapi.draw.drawDetections(canvas, resizedDetections);
//         faceapi.draw.drawFaceLandmarks(canvas, resizedDetections);
//
//         let personFound = false;
//         resizedDetections.forEach(detection => {
//             const box = detection.detection.box;
//             const descriptor = detection.descriptor;
//
//             if (faceMatcher) {
//                 const bestMatch = faceMatcher.findBestMatch(descriptor);
//                 const label = bestMatch.label;
//                 const distance = bestMatch.distance;
//
//                 if (distance <= 0.7) {
//                     const personInfo = personDetails[label] || { age: 'Unknown' };
//                     const currentDate = new Date();
//                     const formattedDate = currentDate.toLocaleDateString('vi-VN');
//                     const formattedTime = currentDate.toLocaleTimeString('vi-VN');
//
//                     infoDiv.textContent = `Detected: ${label}, Age: ${personInfo.age}`;
//                     notificationDiv.textContent = `Face recognized successfully as ${label}!`;
//                     notificationDiv.style.color = 'green';
//                     personFound = true;
//
//                     // Redirect to another page after recognition
//                     setTimeout(() => {
//                         window.location.href = `/attendance-success?name=${label}&age=${personInfo.age}&time=${formattedTime}&date=${formattedDate}`;
//                     }, 1000); // Redirect after 1 second
//
//                     const context = canvas.getContext('2d');
//                     context.font = '16px Arial';
//                     context.fillStyle = 'red';
//                     context.fillText(label, box.x, box.y - 10);
//
//                     context.strokeStyle = 'blue';
//                     context.lineWidth = 2;
//                     context.strokeRect(box.x, box.y, box.width, box.height);
//                 }
//             } else {
//                 console.error("faceMatcher is not defined.");
//             }
//         });
//
//         if (!personFound) {
//             infoDiv.textContent = 'No recognized person detected';
//             notificationDiv.textContent = '';
//         }
//     }, 100);
// }
//
//
// startCameraButton.addEventListener('click', () => {
//     console.log('Start Camera button clicked');
//     init();
// });
//
// function startVideo() {
//     // Request access to the webcam
//     navigator.mediaDevices.getUserMedia({ video: true })
//         .then(stream => {
//             // Set the video source to the webcam stream
//             video.srcObject = stream;
//
//             // Once video metadata is loaded, start playing the video
//             video.onloadedmetadata = () => {
//                 video.play();
//                 // Start detecting faces once the video starts playing
//                 detectFaces();
//             };
//         })
//         .catch(err => {
//             // Handle errors such as permission issues or hardware problems
//             console.error("Error accessing webcam: ", err);
//             alert("Unable to access the camera. Please check your permissions or hardware.");
//         });
// }
// startCameraButton.addEventListener('click', () => {
//     console.log('Start Camera button clicked');
//     startVideo();
// });
//
// document.getElementById('start-camera').addEventListener('click', async () => {
//     // Start the face recognition logic
//     await startFaceRecognition();
// });
//
// async function startFaceRecognition() {
//     // Assuming you've already loaded the models and started the camera
//
//     // Example logic to check for face recognition
//     const video = document.getElementById('video');
//     const detections = await faceapi.detectAllFaces(video).withFaceLandmarks().withFaceDescriptors();
//
//     if (detections.length > 0) {
//         // Assuming you have recognized a face
//         const name = "John Doe";  // Replace this with the recognized name
//         const age = 30;  // Replace with the calculated or input age
//         const time = new Date().toLocaleTimeString();
//         const date = new Date().toLocaleDateString();
//
//         // Redirect to attendance-success.html with the recognized information
//         window.location.href = `/attendance-success?name=${name}&age=${age}&time=${time}&date=${date}`;
//     } else {
//         document.getElementById('notification').textContent = "No face recognized, please try again.";
//     }
// }



const container = document.querySelector('#container');
const video = document.querySelector('#video');
const canvasContainer = document.querySelector('#canvas-container');
const startCameraButton = document.querySelector('#start-camera');
const infoDiv = document.querySelector('#info');
const notificationDiv = document.querySelector('#notification');

let faceMatcher;
const personDetails = {
    'Fukada Eimi': { age: 25 },
    'Rina Ishihara': { age: 30 },
    'Takizawa Laura': { age: 28 },
    'Yua Mikami': { age: 27 },
    'HoanTrong': { age: 19 },
    '123': { age: 20 }
};

async function loadTrainingData() {
    const labels = Object.keys(personDetails);
    const faceDescriptors = [];

    for (const label of labels) {
        const descriptors = [];
        for (let i = 1; i <= 6; i++) {
            try {
                const imageUrl = `/data/${label}/${i}.jpeg`;
                const image = await faceapi.fetchImage(imageUrl);
                await faceapi.nets.tinyFaceDetector.loadFromUri('/models');

                const detection = await faceapi.detectSingleFace(image, new faceapi.TinyFaceDetectorOptions())
                    .withFaceLandmarks().withFaceDescriptor();

                if (detection && detection.descriptor) {
                    descriptors.push(detection.descriptor);
                } else {
                    console.warn(`No face detected in image: ${imageUrl}`);
                }
            } catch (error) {
                console.error(`Error loading image for ${label}:`, error);
            }
        }
        if (descriptors.length > 0) {
            faceDescriptors.push(new faceapi.LabeledFaceDescriptors(label, descriptors));
        } else {
            console.warn(`No valid descriptors found for ${label}.`);
        }
    }
    return faceDescriptors;
}

async function init() {
    try {
        console.log("Loading models...");
        await Promise.all([
            faceapi.nets.ssdMobilenetv1.loadFromUri('/models'),
            faceapi.nets.faceLandmark68Net.loadFromUri('/models'),
            faceapi.nets.faceRecognitionNet.loadFromUri('/models')
        ]);
        console.log("Models loaded successfully");

        const trainingData = await loadTrainingData();
        console.log("Training data loaded:", trainingData);

        if (trainingData.length > 0) {
            faceMatcher = new faceapi.FaceMatcher(trainingData, 0.7);
            console.log("FaceMatcher initialized successfully");
            startVideo();
        } else {
            console.error("No valid training data available. Unable to initialize face recognition.");
            alert("Face recognition could not be initialized due to missing training data.");
        }
    } catch (error) {
        console.error("Error initializing face recognition:", error);
        alert("Failed to initialize face recognition. Please check the console for details.");
    }
}

function startVideo() {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            video.srcObject = stream;
            video.onloadedmetadata = () => {
                video.play();
                detectFaces();
            };
        })
        .catch(err => {
            console.error("Error accessing webcam: ", err);
            alert("Unable to access the camera. Please check your permissions.");
        });
}

async function detectFaces() {
    const canvas = faceapi.createCanvasFromMedia(video);
    canvasContainer.innerHTML = '';
    canvasContainer.append(canvas);

    const displaySize = { width: video.width, height: video.height };
    faceapi.matchDimensions(canvas, displaySize);

    setInterval(async () => {
        const detections = await faceapi.detectAllFaces(video)
            .withFaceLandmarks().withFaceDescriptors();
        const resizedDetections = faceapi.resizeResults(detections, displaySize);

        canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height);
        faceapi.draw.drawDetections(canvas, resizedDetections);
        faceapi.draw.drawFaceLandmarks(canvas, resizedDetections);

        let personFound = false;
        resizedDetections.forEach(detection => {
            const box = detection.detection.box;
            const descriptor = detection.descriptor;

            if (faceMatcher) {
                const bestMatch = faceMatcher.findBestMatch(descriptor);
                const label = bestMatch.label;
                const distance = bestMatch.distance;

                if (distance <= 0.7) {
                    const personInfo = personDetails[label] || { age: 'Unknown' };
                    const currentDate = new Date();
                    const formattedDate = currentDate.toLocaleDateString('vi-VN');
                    const formattedTime = currentDate.toLocaleTimeString('vi-VN');

                    infoDiv.textContent = `Detected: ${label}, Age: ${personInfo.age}`;
                    notificationDiv.textContent = `Face recognized successfully as ${label}!`;
                    notificationDiv.style.color = 'green';
                    personFound = true;

                    // Redirect to another page after recognition
                    setTimeout(() => {
                        window.location.href = `/attendance-success?name=${label}&age=${personInfo.age}&time=${formattedTime}&date=${formattedDate}`;
                    }, 1000); // Redirect after 1 second

                    const context = canvas.getContext('2d');
                    context.font = '16px Arial';
                    context.fillStyle = 'red';
                    context.fillText(label, box.x, box.y - 10);

                    context.strokeStyle = 'blue';
                    context.lineWidth = 2;
                    context.strokeRect(box.x, box.y, box.width, box.height);
                }
            } else {
                console.error("faceMatcher is not defined.");
            }
        });

        if (!personFound) {
            infoDiv.textContent = 'No recognized person detected';
            notificationDiv.textContent = '';
        }
    }, 100);
}

function startVideo() {
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            video.srcObject = stream;
            video.onloadedmetadata = () => {
                video.play();
                detectFaces(); // Ensure this is defined properly
            };
        })
        .catch(err => {
            console.error("Error accessing webcam: ", err);
            alert("Unable to access the camera. Please check your permissions.");
        });
}
startCameraButton.addEventListener('click', () => {
    console.log('Start Camera button clicked');
    startVideo();
});

startCameraButton.addEventListener('click', () => {
    console.log('Start Camera button clicked');
    init();
});
async function startFaceRecognition() {
//     // Assuming you've already loaded the models and started the camera
//
//     // Example logic to check for face recognition
    const video = document.getElementById('video');
    const detections = await faceapi.detectAllFaces(video).withFaceLandmarks().withFaceDescriptors();

    if (detections.length > 0) {
        // Assuming you have recognized a face
        const name = "John Doe";  // Replace this with the recognized name
        const age = 30;  // Replace with the calculated or input age
        const time = new Date().toLocaleTimeString();
        const date = new Date().toLocaleDateString();

        // Redirect to attendance-success.html with the recognized information
        window.location.href = `/attendance-success?name=${name}&age=${age}&time=${time}&date=${date}`;
    } else {
        document.getElementById('notification').textContent = "No face recognized, please try again.";
    }
}
