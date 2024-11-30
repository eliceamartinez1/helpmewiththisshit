function fetchData() {
    // This will call /data without any query parameters
    fetch('http://localhost:8080/data')
        .then(response => response.text())
        .then(data => {
            // Display the response in the HTML
            document.getElementById('response').innerText = data;
        })
        .catch(error => console.error('Error:', error));
}
// script.js

document.addEventListener('DOMContentLoaded', function() {
    // Get elements after the DOM is fully loaded
    const openSignupBtn = document.getElementById('openSignupBtn');
    const signupPopup = document.getElementById('signupPopup');
    const closeSignupBtn = document.querySelector('.signup-close-btn');

    // Function to open popup on button click
    openSignupBtn.addEventListener('click', function() {
        signupPopup.style.display = 'flex';  // Show popup when button is clicked
    });

    // Function to close popup on close button click
    closeSignupBtn.addEventListener('click', function() {
        signupPopup.style.display = 'none';  // Hide popup when close button is clicked
    });

    // Close popup if user clicks outside of the popup content
    window.addEventListener('click', function(event) {
        if (event.target == signupPopup) {
            signupPopup.style.display = 'none';  // Hide popup when clicking outside the content
        }
    });
});

function goHome() {
    window.location.href = 'index.html';
}

function signUp() {
    window.location.href = 'signup.html';
}

// Function to open the search pop-up
function openSearch() {
    document.getElementById('searchPopup').style.display = 'flex';
}

// Function to close the search pop-up
function closeSearch() {
    document.getElementById('searchPopup').style.display = 'none';
}

function key_up(e) {
    e = e || window.event
    if(e.keyCode ==13) {
        submitSearch();
    }
}


// Function to submit the search query and navigate to the search results page
function submitSearch() {
    var query = document.getElementById('searchInput').value;

    // Store the search query in localStorage to use on the search results page
    localStorage.setItem('searchQuery', query);

    // Fetch request for search results from the backend (search by product name)
    fetch(`http://localhost:8080/data?query=${query}`)  // Call with product name
        .then(response => response.json())
        .then(data => {
            console.log('Search results:', data);  // Log or handle the search results here
            document.getElementById('response').innerText = JSON.stringify(data);
        })
        .catch(error => console.error('Error:', error));

    // Navigate to the search.html page
    window.location.href = 'search.html';
}

// Function to display the search query in search.html
window.onload = function() {
    var productName = localStorage.getItem('searchQuery');  // Retrieve product name
    if (productName) {
        // Make a fetch request to get search results based on the product name
        fetch(`http://localhost:8080/data?query=${productName}`)
            .then(response => response.json())
            .then(data => {
                console.log('Fetched search data:', data);
                const searchResultsDiv = document.getElementById('searchResults');

                // Check if we got any results
                if (data.length > 0) {
                    // Iterate through the results and display each review
                    data.forEach(review => {
                        const resultRow = document.createElement('div');
                        resultRow.classList.add('result-row');
                        resultRow.innerText = review;  // Each review is displayed as a row
                        searchResultsDiv.appendChild(resultRow);
                    });
                } else {
                    // If no results are found, display a message
                    searchResultsDiv.innerText = "No reviews found for this product.";
                }
            })
            .catch(error => {
                console.error('Error fetching search results:', error);
                document.getElementById('searchResults').innerText = "Error fetching search results.";
            });
    } else {
        document.getElementById('searchResults').innerText = "No search query provided.";
    }
};
