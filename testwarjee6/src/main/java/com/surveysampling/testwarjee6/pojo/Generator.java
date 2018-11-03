package com.surveysampling.testwarjee6.pojo;

import java.util.Random;
import java.util.logging.Logger;

import com.surveysampling.testwarjee6.FacesLoggerFactory;



public class Generator {
	private final int max;
	private final Random random = new Random();
	private final Logger logger = FacesLoggerFactory.createFacesLogger();

	public Generator(final int max) {
		this.logger.info("Generator.<ctor>");
		if (max <= 0) {
			throw new IllegalArgumentException();
		}
		this.max = max;
	}

	/**
	 * Gets a random number between 1 and getMaxNumber, inclusive.
	 */
	public int getNext() {
		final int r = this.random.nextInt(this.max) + 1;
		this.logger.info("picked random number: "+r);

		// sanity check
		if (r < 1 || this.max < r) {
			throw new IllegalStateException("Illegal random number generated: "+r+" Must be between 1 and "+this.max+", inclusive.");
		}

		return r;
	}

	public int getMax() {
		return this.max;
	}

	@Override
	protected void finalize() {
		this.logger.info("Generator.finalize");
	}

}
