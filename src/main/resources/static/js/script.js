
// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Quantity input controls
    const quantityInputs = document.querySelectorAll('.quantity-control');
    
    if (quantityInputs) {
        quantityInputs.forEach(function(control) {
            const decreaseBtn = control.querySelector('.decrease-qty');
            const increaseBtn = control.querySelector('.increase-qty');
            const input = control.querySelector('input[type="number"]');
            
            if (decreaseBtn && increaseBtn && input) {
                decreaseBtn.addEventListener('click', function() {
                    let currentValue = parseInt(input.value);
                    if (currentValue > 1) {
                        input.value = currentValue - 1;
                        // Trigger change event for forms that need to update
                        input.dispatchEvent(new Event('change'));
                    }
                });
                
                increaseBtn.addEventListener('click', function() {
                    let currentValue = parseInt(input.value);
                    let max = parseInt(input.getAttribute('max')) || 99;
                    if (currentValue < max) {
                        input.value = currentValue + 1;
                        // Trigger change event for forms that need to update
                        input.dispatchEvent(new Event('change'));
                    }
                });
                
                // Ensure value is within min-max range
                input.addEventListener('change', function() {
                    let min = parseInt(input.getAttribute('min')) || 1;
                    let max = parseInt(input.getAttribute('max')) || 99;
                    let currentValue = parseInt(input.value) || min;
                    
                    if (currentValue < min) input.value = min;
                    if (currentValue > max) input.value = max;
                });
            }
        });
    }
    
    // Cart quantity update - Auto submit form when quantity changes
    const cartQuantityForms = document.querySelectorAll('.cart-update-form');
    
    if (cartQuantityForms) {
        cartQuantityForms.forEach(function(form) {
            const quantityInput = form.querySelector('input[name="quantity"]');
            
            if (quantityInput) {
                quantityInput.addEventListener('change', function() {
                    form.submit();
                });
            }
        });
    }
    
    // Book search filter controls
    const filterForm = document.getElementById('filterForm');
    const resetFilterBtn = document.getElementById('resetFilter');
    
    if (filterForm && resetFilterBtn) {
        resetFilterBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Clear all filter inputs
            const inputs = filterForm.querySelectorAll('input:not([type="submit"]), select');
            inputs.forEach(function(input) {
                if (input.type === 'checkbox' || input.type === 'radio') {
                    input.checked = false;
                } else {
                    input.value = '';
                }
            });
            
            // Submit the form with cleared filters
            filterForm.submit();
        });
    }
    
    // Book detail image gallery
    const mainImage = document.getElementById('mainBookImage');
    const thumbnails = document.querySelectorAll('.book-thumbnail');
    
    if (mainImage && thumbnails.length > 0) {
        thumbnails.forEach(function(thumb) {
            thumb.addEventListener('click', function() {
                // Update main image src
                mainImage.src = this.getAttribute('data-image');
                
                // Update active state
                thumbnails.forEach(t => t.classList.remove('active'));
                this.classList.add('active');
            });
        });
    }
    
    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    
    if (forms) {
        Array.from(forms).forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                
                form.classList.add('was-validated');
            }, false);
        });
    }
    
    // Auto-hide alert messages after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    
    if (alerts) {
        alerts.forEach(function(alert) {
            setTimeout(function() {
                // Use Bootstrap's alert dismiss method if available
                if (typeof bootstrap !== 'undefined') {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                } else {
                    // Manual fade out
                    alert.style.transition = 'opacity 1s';
                    alert.style.opacity = '0';
                    setTimeout(function() {
                        alert.style.display = 'none';
                    }, 1000);
                }
            }, 5000);
        });
    }
}); 