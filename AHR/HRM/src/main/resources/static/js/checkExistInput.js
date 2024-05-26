
 var checkPhoneURL = 'http://localhost:8080/check/user/phone';
 var checkEmailURL = 'http://localhost:8080/check/user/email';

 async function validateEmail(emailAddress,emailError,buttonId,currentEmail) {
     var emailInput = document.getElementById(emailAddress);
     var email = emailInput.value;
     var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
     var errorElement = document.getElementById(emailError);
     var submitButton = document.getElementById(buttonId);
     if (email == currentEmail) {
         errorElement.innerText = '';
         submitButton.disabled = false;
         return true;
     }
     // Kiểm tra định dạng email
     if (!emailRegex.test(email)) {
         errorElement.innerText = 'Please enter a valid email address.';
         emailInput.focus();
         return false; // Email không hợp lệ
     }

     // Xóa lỗi định dạng trước khi kiểm tra tính duy nhất
     errorElement.innerText = '';

     // Kiểm tra tính duy nhất của email
     var dataEmail = {email: email};
     var isUnique = await checkUnique(checkEmailURL, dataEmail, errorElement, `Email ${email} đã tồn tại.`);

     return isUnique;
 }
 async function validatePhone(phoneNumber,phoneError,buttonId,currentPhoneNumber) {
     console.log("Starting validatePhone function");

     // Getting the phone number input element and value
     var phoneInput = document.getElementById(phoneNumber);
     var phone = phoneInput.value;
     var phoneRegex = /^[0-9]+$/; // Adjust regex to fit your phone number format
     var errorElement = document.getElementById(phoneError);
     var submitButton = document.getElementById(buttonId);
     console.log(phone)
     console.log(currentPhoneNumber)
     if (phone == currentPhoneNumber) {
         errorElement.innerText = '';
         submitButton.disabled = false;
         return true;
     }
     // Validate phone number format using a regex pattern
     if (!phoneRegex.test(phone) || phone.length !== 10) {
         errorElement.innerText = 'Please enter a valid phone number (10 digits).';
         phoneInput.focus();
         submitButton.disabled = true; // Disable the submit button
         return false; // Invalid phone number format or length
     }

     // Clear format errors before checking for uniqueness
     errorElement.innerText = '';
     submitButton.disabled = false; // Disable the submit button

     // Check the uniqueness of the phone number
     var dataPhone = { phone: phone };
     var isUnique = await checkUnique(checkPhoneURL, dataPhone, errorElement, `Phone number already exists.`);
     if (!isUnique) {
         submitButton.disabled = true; // Disable the submit button if phone number is not unique
     } else {
         submitButton.disabled = false; // Enable the submit button if phone number is unique
     }
     return isUnique; // Return true if the phone is valid and unique, otherwise false
 }
 function checkUnique(url, data, errorElement, message) {
     console.log("Starting checkUnique function");
     console.log("URL:", url);
     console.log("Data to be sent:", data);

     return new Promise(function (resolve, reject) {
         $.ajax({
             url: url,
             type: "GET",
             data: data,
             success: function (response) {
                 console.log("Received response from server:", response);

                 if (response === "TRUNG") {
                     console.log("Response indicates duplicate found.");
                     errorElement.innerText = message; // Set the error message
                     resolve(false); // Indicate the item is not unique
                 } else {
                     console.log("Response indicates no duplicate found.");
                     errorElement.innerText = ''; // Clear any error message
                     resolve(true); // Indicate the item is unique
                 }
             },
             error: function (ex) {
                 console.error("Error during AJAX request:", ex);
                 alert("Can not connect to server.");
                 reject(ex); // Indicate failure of the API call
             }
         });
     });
 }
