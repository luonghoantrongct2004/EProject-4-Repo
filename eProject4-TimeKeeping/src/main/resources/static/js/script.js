const container = document.querySelector('#container');
const video = document.querySelector('#video');
const canvasContainer = document.querySelector('#canvas-container');
const startCameraButton = document.querySelector('#start-camera');
const infoDiv = document.querySelector('#info');
const notificationDiv = document.querySelector('#notification');

// Mapping chi tiết người dùng với Account ID
const personDetails = {
    'Fukada Eimi': { age: 25, accountId: 4 },
    'Rina Ishihara': { age: 30, accountId: 2 },
    'Takizawa Laura': { age: 28, accountId: 3 },
    'Yua Mikami': { age: 27, accountId: 5 },
    'HoanTrong': { age: 19, accountId: 1 }
};

let clockInStatus = {};  // Theo dõi trạng thái đã chấm công
let faceMatcher;

// Bắt đầu nhận diện khuôn mặt
async function init() {
    try {
        console.log("Đang tải các mô hình nhận diện khuôn mặt...");
        await Promise.all([
            faceapi.nets.ssdMobilenetv1.loadFromUri('/models'),
            faceapi.nets.faceLandmark68Net.loadFromUri('/models'),
            faceapi.nets.faceRecognitionNet.loadFromUri('/models')
        ]);

        const trainingData = await loadTrainingData();
        console.log("Dữ liệu nhận diện khuôn mặt đã được tải:", trainingData);

        if (trainingData.length > 0) {
            faceMatcher = new faceapi.FaceMatcher(trainingData, 0.7);
            startVideo();
        } else {
            alert("Không có dữ liệu khuôn mặt hợp lệ.");
        }
    } catch (error) {
        console.error("Lỗi khi tải mô hình nhận diện: ", error);
    }
}

// Tải dữ liệu nhận diện khuôn mặt từ ảnh
async function loadTrainingData() {
    const labels = Object.keys(personDetails);
    const faceDescriptors = [];

    for (const label of labels) {
        const descriptors = [];
        for (let i = 1; i <= 6; i++) {
            try {
                const imageUrl = `/data/${label}/${i}.jpeg`;
                const image = await faceapi.fetchImage(imageUrl);
                const detection = await faceapi.detectSingleFace(image).withFaceLandmarks().withFaceDescriptor();

                if (detection && detection.descriptor) {
                    descriptors.push(detection.descriptor);
                }
            } catch (error) {
                console.error(`Lỗi tải ảnh cho ${label}: ${error}`);
            }
        }
        if (descriptors.length > 0) {
            faceDescriptors.push(new faceapi.LabeledFaceDescriptors(label, descriptors));
        }
    }
    return faceDescriptors;
}

// Bắt đầu camera
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
            alert("Không thể truy cập camera: " + err);
        });
}

// Nhận diện khuôn mặt và ghi nhận chấm công
async function detectFaces() {
    const canvas = faceapi.createCanvasFromMedia(video);
    canvasContainer.innerHTML = '';
    canvasContainer.append(canvas);

    const displaySize = { width: video.videoWidth, height: video.videoHeight };
    faceapi.matchDimensions(canvas, displaySize);

    setInterval(async () => {
        const detections = await faceapi.detectAllFaces(video).withFaceLandmarks().withFaceDescriptors();
        const resizedDetections = faceapi.resizeResults(detections, displaySize);

        canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height);
        faceapi.draw.drawDetections(canvas, resizedDetections);
        faceapi.draw.drawFaceLandmarks(canvas, resizedDetections);

        let personFound = false;
        resizedDetections.forEach(detection => {
            const descriptor = detection.descriptor;
            if (faceMatcher) {
                const bestMatch = faceMatcher.findBestMatch(descriptor);
                const label = bestMatch.label;

                if (personDetails[label]) {
                    handleFaceRecognition(personDetails[label].accountId);
                    personFound = true;
                }
            }
        });

        if (!personFound) {
            infoDiv.textContent = 'Không phát hiện khuôn mặt hợp lệ';
            notificationDiv.textContent = '';
        }
    }, 1000);
}

// Ghi nhận chấm công (Clock In/Out)
async function handleFaceRecognition(accountID) {
    const currentTime = new Date();
    const currentHour = currentTime.getHours();
    const currentMinutes = currentTime.getMinutes(); // Lấy phút hiện tại

    const alreadyClockedIn = clockInStatus[accountID] === true; // Kiểm tra nếu đã chấm công

    try {
        if (!alreadyClockedIn) {
            // Nếu chưa chấm công, tiến hành chấm công vào (clockin)
            await recordAttendance(accountID, 'clockin');
            clockInStatus[accountID] = {
                status: true,   // Đánh dấu đã chấm công
                clockInTime: currentTime // Lưu thời gian chấm công vào
            };
            console.log(`Người dùng ${accountID} đã chấm công vào lúc ${currentHour}:${currentMinutes}`);
        } else {
            // Nếu đã chấm công vào, chuyển sang chấm công ra (clockout)
            await recordAttendance(accountID, 'clockout');

            // Tính thời gian làm việc
            const clockInTime = clockInStatus[accountID].clockInTime;
            const workedHours = (currentTime - new Date(clockInTime)) / (1000 * 60 * 60); // Tính giờ làm việc

            delete clockInStatus[accountID];  // Xóa trạng thái chấm công
            console.log(`Người dùng ${accountID} đã chấm công ra lúc ${currentHour}:${currentMinutes}. Thời gian làm việc: ${workedHours.toFixed(2)} giờ`);
        }
    } catch (error) {
        console.error(`Lỗi khi ghi nhận chấm công cho người dùng ${accountID}: `, error);
        alert('Lỗi khi ghi nhận chấm công. Vui lòng thử lại.');
    }
}



// Gửi yêu cầu chấm công (Clock In/Out)
async function recordAttendance(accountID, action) {
    let endpoint = '';
    if (action === 'clockin') {
        endpoint = `/attendance/autoClock?accountID=${accountID}`;  // API cho clockin
    } else if (action === 'clockout') {
        endpoint = `/attendance/autoClock?accountID=${accountID}`;  // API cho clockout
    }

    try {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (response.ok) {
            console.log(`${action} thành công`);
            window.location.href = `/attendance/show?accountID=${accountID}`;  // Chuyển hướng tới trang xem bản ghi
        } else {
            console.error(`Ghi nhận ${action} thất bại: `, response.statusText);
            alert("Lỗi: " + response.statusText);
        }
    } catch (error) {
        console.error(`Lỗi khi ghi nhận ${action}:`, error);
        alert("Lỗi hệ thống: " + error.message);
    }
}

// Khởi tạo nhận diện khi nhấn nút
startCameraButton.addEventListener('click', () => {
    console.log('Start Camera button clicked');
    init();
    startVideo();
});

