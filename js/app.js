/**
 * Main App - Orchestrates camera, orientation, and renderer modules
 */

class HorizonLockApp {
    constructor() {
        // DOM elements
        this.elements = {};
        
        // Module instances
        this.camera = null;
        this.orientation = null;
        this.renderer = null;
        
        // App state
        this.isInitialized = false;
        this.isRunning = false;
        this.error = null;
        
        // Bind methods
        this.handleOrientationUpdate = this.handleOrientationUpdate.bind(this);
        this.handleVisibilityChange = this.handleVisibilityChange.bind(this);
        this.handleResize = this.handleResize.bind(this);
        
        // Initialize when DOM is ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.init());
        } else {
            this.init();
        }
    }

    /**
     * Cache DOM element references
     */
    cacheElements() {
        this.elements = {
            app: document.getElementById('app'),
            video: document.getElementById('camera-video'),
            permissionOverlay: document.getElementById('permission-overlay'),
            startBtn: document.getElementById('start-btn'),
            permissionError: document.getElementById('permission-error'),
            controls: document.getElementById('controls'),
            toggleCamera: document.getElementById('toggle-camera'),
            toggleStabilization: document.getElementById('toggle-stabilization'),
            captureBtn: document.getElementById('capture-btn'),
            settingsPanel: document.getElementById('settings-panel'),
            settingsToggle: document.getElementById('settings-toggle'),
            closeSettings: document.getElementById('close-settings'),
            zoomSlider: document.getElementById('zoom-slider'),
            zoomValue: document.getElementById('zoom-value'),
            smoothingSlider: document.getElementById('smoothing-slider'),
            smoothingValue: document.getElementById('smoothing-value'),
            status: document.getElementById('status'),
            statusText: document.getElementById('status-text'),
            angleDisplay: document.getElementById('angle-display'),
            angleValue: document.getElementById('angle-value'),
            canvas: document.getElementById('capture-canvas')
        };
    }

    /**
     * Initialize the app
     */
    async init() {
        try {
            this.cacheElements();
            this.setupEventListeners();
            
            // Check for required APIs
            if (!this.checkRequirements()) {
                return;
            }
            
            // Initialize modules
            this.camera = new CameraManager();
            this.orientation = new OrientationManager();
            this.renderer = new Renderer(this.elements.video);
            
            // Check if orientation requires permission on load
            const needsPermission = await this.orientation.requiresPermission();
            if (!needsPermission) {
                // On Android/Desktop, we can pre-initialize
                this.elements.startBtn.textContent = 'Start Camera';
            } else {
                this.elements.startBtn.textContent = 'Allow Access';
            }
            
            this.isInitialized = true;
            this.updateStatus('Ready to start');
            
        } catch (error) {
            console.error('Error initializing app:', error);
            this.showError('Failed to initialize: ' + error.message);
        }
    }

    /**
     * Check browser requirements
     */
    checkRequirements() {
        const checks = {
            mediaDevices: !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia),
            deviceOrientation: 'DeviceOrientationEvent' in window,
            secureContext: window.isSecureContext
        };

        if (!checks.secureContext) {
            this.showError('This app requires HTTPS to access camera and sensors.');
            return false;
        }

        if (!checks.mediaDevices) {
            this.showError('Camera not supported on this browser.');
            return false;
        }

        if (!checks.deviceOrientation) {
            this.showError('Device orientation not supported. Stabilization will be disabled.');
        }

        return true;
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Start button
        this.elements.startBtn.addEventListener('click', () => this.start());
        
        // Camera toggle
        this.elements.toggleCamera.addEventListener('click', () => this.toggleCamera());
        
        // Stabilization toggle
        this.elements.toggleStabilization.addEventListener('click', () => this.toggleStabilization());
        
        // Capture button
        this.elements.captureBtn.addEventListener('click', () => this.capture());
        
        // Settings panel
        this.elements.settingsToggle.addEventListener('click', () => this.showSettings());
        this.elements.closeSettings.addEventListener('click', () => this.hideSettings());
        
        // Zoom slider
        this.elements.zoomSlider.addEventListener('input', (e) => {
            const value = parseFloat(e.target.value);
            this.elements.zoomValue.textContent = value.toFixed(1) + 'x';
            if (this.renderer) {
                this.renderer.setZoom(value);
            }
        });
        
        // Smoothing slider
        this.elements.smoothingSlider.addEventListener('input', (e) => {
            const value = parseFloat(e.target.value);
            this.elements.smoothingValue.textContent = value.toFixed(2);
            if (this.renderer) {
                this.renderer.setSmoothingFactor(value);
            }
        });
        
        // Visibility change
        document.addEventListener('visibilitychange', this.handleVisibilityChange);
        
        // Resize
        window.addEventListener('resize', this.handleResize);
        
        // Prevent zoom on double tap
        let lastTouchEnd = 0;
        document.addEventListener('touchend', (e) => {
            const now = Date.now();
            if (now - lastTouchEnd <= 300) {
                e.preventDefault();
            }
            lastTouchEnd = now;
        }, false);
    }

    /**
     * Start the app
     */
    async start() {
        if (this.isRunning) return;
        
        this.elements.startBtn.disabled = true;
        this.elements.startBtn.textContent = 'Starting...';
        this.updateStatus('Requesting permissions...');
        
        try {
            // Initialize camera
            await this.camera.init(this.elements.video);
            
            // Initialize orientation (may prompt for permission on iOS)
            await this.orientation.init();
            this.orientation.addListener(this.handleOrientationUpdate);
            
            // Start renderer
            this.renderer.start();
            
            // Hide permission overlay and show controls
            this.elements.permissionOverlay.classList.remove('active');
            this.elements.controls.classList.remove('hidden');
            this.elements.status.classList.remove('hidden');
            this.elements.angleDisplay.classList.remove('hidden');
            
            this.isRunning = true;
            this.updateStatus('Camera active');
            
        } catch (error) {
            console.error('Error starting app:', error);
            this.showError(this.getErrorMessage(error));
            this.elements.startBtn.disabled = false;
            this.elements.startBtn.textContent = 'Try Again';
        }
    }

    /**
     * Handle orientation updates from the orientation manager
     */
    handleOrientationUpdate(data) {
        if (this.renderer) {
            // Use horizon angle for stabilization
            this.renderer.updateRotation(data.horizonAngle);
        }
        
        // Update angle display
        if (this.elements.angleValue) {
            const angle = Math.round(data.horizonAngle);
            this.elements.angleValue.textContent = `${angle}°`;
        }
    }

    /**
     * Toggle between front and back camera
     */
    async toggleCamera() {
        try {
            this.updateStatus('Switching camera...');
            await this.camera.toggleCamera();
            this.updateStatus('Camera switched');
        } catch (error) {
            console.error('Error switching camera:', error);
            this.updateStatus('Failed to switch camera');
        }
    }

    /**
     * Toggle stabilization on/off
     */
    toggleStabilization() {
        if (this.renderer) {
            const enabled = this.renderer.toggleStabilization();
            this.elements.toggleStabilization.classList.toggle('active', enabled);
            this.updateStatus(enabled ? 'Stabilization on' : 'Stabilization off');
        }
    }

    /**
     * Capture current frame
     */
    capture() {
        if (!this.renderer) return;
        
        try {
            // Create flash effect
            const flash = document.createElement('div');
            flash.className = 'flash-overlay';
            this.elements.app.appendChild(flash);
            
            setTimeout(() => flash.remove(), 350);
            
            // Capture the frame
            const canvas = this.renderer.captureFrame(this.elements.canvas);
            
            // Convert to blob and download
            canvas.toBlob((blob) => {
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = `horizon-capture-${Date.now()}.jpg`;
                link.click();
                URL.revokeObjectURL(url);
                
                this.updateStatus('Photo saved');
            }, 'image/jpeg', 0.95);
            
        } catch (error) {
            console.error('Error capturing:', error);
            this.updateStatus('Capture failed');
        }
    }

    /**
     * Show settings panel
     */
    showSettings() {
        this.elements.settingsPanel.classList.remove('hidden');
    }

    /**
     * Hide settings panel
     */
    hideSettings() {
        this.elements.settingsPanel.classList.add('hidden');
    }

    /**
     * Handle visibility change (pause/resume)
     */
    handleVisibilityChange() {
        if (document.hidden) {
            // Pause when hidden
            if (this.camera) this.camera.pause();
            if (this.renderer) this.renderer.stop();
        } else {
            // Resume when visible
            if (this.camera) this.camera.resume();
            if (this.renderer && this.isRunning) this.renderer.start();
        }
    }

    /**
     * Handle window resize
     */
    handleResize() {
        // Force update of transforms
        if (this.renderer) {
            this.renderer.updateTransform();
        }
    }

    /**
     * Update status text
     */
    updateStatus(message) {
        if (this.elements.statusText) {
            this.elements.statusText.textContent = message;
        }
    }

    /**
     * Show error message
     */
    showError(message) {
        this.error = message;
        if (this.elements.permissionError) {
            this.elements.permissionError.textContent = message;
        }
        console.error(message);
    }

    /**
     * Get user-friendly error message
     */
    getErrorMessage(error) {
        const errorMessages = {
            'NotAllowedError': 'Camera access denied. Please allow camera access in your browser settings.',
            'NotFoundError': 'No camera found on this device.',
            'NotReadableError': 'Camera is in use by another application.',
            'OverconstrainedError': 'Camera does not support the requested settings.',
            'SecurityError': 'Camera access blocked for security reasons.',
            'AbortError': 'Camera request was cancelled.',
            'Permission denied for device orientation': 'Orientation access denied. Please allow motion access in settings.'
        };
        
        return errorMessages[error.name] || error.message || 'An unknown error occurred';
    }

    /**
     * Cleanup and destroy app
     */
    destroy() {
        this.isRunning = false;
        
        if (this.camera) {
            this.camera.destroy();
            this.camera = null;
        }
        
        if (this.orientation) {
            this.orientation.destroy();
            this.orientation = null;
        }
        
        if (this.renderer) {
            this.renderer.destroy();
            this.renderer = null;
        }
        
        document.removeEventListener('visibilitychange', this.handleVisibilityChange);
        window.removeEventListener('resize', this.handleResize);
    }
}

// Start the app
const app = new HorizonLockApp();
