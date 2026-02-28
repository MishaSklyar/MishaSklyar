/**
 * Smoothing module - Exponential moving average for angle interpolation
 * Handles angle wrapping for smooth 360° transitions
 */

class AngleSmoother {
    constructor(smoothingFactor = 0.15) {
        this.smoothingFactor = smoothingFactor;
        this.currentAngle = 0;
        this.targetAngle = 0;
        this.isInitialized = false;
    }

    /**
     * Set the smoothing factor (0.0 - 1.0)
     * Lower values = smoother but more lag
     * Higher values = more responsive but less smooth
     */
    setSmoothingFactor(factor) {
        this.smoothingFactor = Math.max(0.01, Math.min(1, factor));
    }

    /**
     * Initialize the smoother with a starting angle
     */
    initialize(angle) {
        this.currentAngle = angle;
        this.targetAngle = angle;
        this.isInitialized = true;
    }

    /**
     * Normalize angle to -180 to 180 range for shortest path calculation
     */
    normalizeAngle(angle) {
        angle = angle % 360;
        if (angle > 180) angle -= 360;
        if (angle < -180) angle += 360;
        return angle;
    }

    /**
     * Update target angle and compute smoothed value
     * Handles angle wrapping to ensure smooth transitions across 0°/360°
     */
    update(targetAngle) {
        if (!this.isInitialized) {
            this.initialize(targetAngle);
            return targetAngle;
        }

        this.targetAngle = targetAngle;

        // Calculate shortest path between current and target
        let delta = this.targetAngle - this.currentAngle;
        delta = this.normalizeAngle(delta);

        // Apply exponential smoothing
        const smoothedDelta = delta * this.smoothingFactor;
        this.currentAngle += smoothedDelta;

        // Normalize result to 0-360 range
        this.currentAngle = this.currentAngle % 360;
        if (this.currentAngle < 0) this.currentAngle += 360;

        return this.currentAngle;
    }

    /**
     * Get current smoothed angle
     */
    getCurrentAngle() {
        return this.currentAngle;
    }

    /**
     * Reset the smoother
     */
    reset() {
        this.isInitialized = false;
        this.currentAngle = 0;
        this.targetAngle = 0;
    }
}

/**
 * Low-pass filter for reducing noise in sensor readings
 */
class LowPassFilter {
    constructor(alpha = 0.2) {
        this.alpha = alpha;
        this.filteredValue = null;
    }

    setAlpha(alpha) {
        this.alpha = Math.max(0, Math.min(1, alpha));
    }

    filter(value) {
        if (this.filteredValue === null) {
            this.filteredValue = value;
            return value;
        }

        this.filteredValue = this.alpha * value + (1 - this.alpha) * this.filteredValue;
        return this.filteredValue;
    }

    reset() {
        this.filteredValue = null;
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { AngleSmoother, LowPassFilter };
}
