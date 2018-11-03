package com.surveysampling.testwarjee6;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.example.LibClass1;
import com.example.LibClass2;

@Named
@SessionScoped
public class UserNumber implements Serializable {
	private int number;
	private int userNumber;
	private int remainingGuesses;

	@Inject @MaxNumber
	private int maxNumber;

	@Resource
	private int allowedGuesses;

	private int minimum;
	private int maximum;

	@Inject @RandomNumber
	private Instance<Integer> random;

	private boolean higher;
	private boolean lower;
	private boolean knownAlready;
	private boolean userHasGuessed;

	private String[] notes = {"abc","def"};

	public String[] getNotes() {
		return this.notes;
	}
	@PostConstruct
	public void postConstruct() {
		reset();
	}

	public String logOut() {
		HttpSession ses = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ses.invalidate();
		return "bye";
	}

	public int getNumber() {
		return this.number;
	}

	public int getLastUserNumber() {
		assertUserHasGuessed();
		return this.userNumber;
	}

	public int getUserNumber() {
		return getSuggestedGuess();
	}

	public void setUserNumber(final int userNumber) {
		this.userNumber = userNumber;
	}

	public int getMaximum() {
		return (this.maximum);
	}

	public int getMinimum() {
		return (this.minimum);
	}

	public int getRemainingGuesses() {
		return this.remainingGuesses;
	}

	public boolean isAlreadyKnown() {
		assertUserHasGuessed();
		return this.knownAlready;
	}

	public boolean isHigher() {
		assertUserHasGuessed();
		assertHigherOrLower();
		return this.higher;
	}

	public boolean isLower() {
		assertUserHasGuessed();
		assertHigherOrLower();
		return this.lower;
	}

	private void assertHigherOrLower() {
		if (!(this.higher || this.lower)) {
			throw new IllegalStateException();
		}
	}

	private void assertValidMinMaxNumber() {
		if (this.number < this.minimum || this.maximum < this.number) {
			throw new IllegalStateException();
		}
	}

	private void assertUserHasGuessed() throws IllegalStateException {
		if (!this.userHasGuessed) {
			throw new IllegalStateException();
		}
	}

	public int getSuggestedGuess() {
		return (int) Math.rint(Math.rint(this.maximum + this.minimum) / 2);
	}

	public String guess() {
	  LibClass1 c1 = new LibClass1();
	  LibClass2 c2 = new LibClass2();
	  LibClass1 c1x = new LibClass1();

    c1.something();
    c2.something();
    c1x.something();
		this.userHasGuessed = true;

		this.higher = (this.userNumber < this.number);
		this.lower = (this.number < this.userNumber);
		this.knownAlready = (this.userNumber < this.minimum || this.maximum < this.userNumber);

		--this.remainingGuesses;

		if (!this.knownAlready) {
			if (this.userNumber > this.number) {
				this.maximum = this.userNumber - 1;
			}
			if (this.userNumber < this.number) {
				this.minimum = this.userNumber + 1;
			}
			assertValidMinMaxNumber();
		}

		return getNextPage();
	}

	/**
	 * @return
	 */
	private String getNextPage() {
		final String nextPage;
		if (!(this.higher || this.lower)) {
			nextPage = "win"; // note: MUST check for win FIRST
		} else if (this.remainingGuesses <= 0) {
			nextPage = "lose";
		} else {
			nextPage = "guess";
		}
		return nextPage;
	}

	public String reset() {
		this.userHasGuessed = false;
		this.remainingGuesses = this.allowedGuesses;
		this.minimum = 1;
		this.maximum = this.maxNumber;
		this.number = this.random.get().intValue();

		assertValidMinMaxNumber();

		return "index";
	}
}
