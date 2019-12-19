/**
 * @file EulidMinimax.cpp
 * @authors Carolin, Dominik
 * @brief Solution to sheet 5, exercise 3 of the course Introduction to Artificial Intelligence.
 */

#include <iostream>
#include <string>
#include <vector>
#include <limits>
#include <algorithm>

//#define DOMJUDGE                                    /* uncomment this to enable DomJugde mode           */

#ifndef DOMJUDGE
#include <fstream>
#endif

enum Results {
    WIN_STAN=1, WIN_OLLIE=-1
};

enum Player {
    NONE, STAN, OLLIE
};

class State {
    public:
        State() {
            smallNumber = 0;
            bigNumber = 0;
            player = NONE;
        }

        State(unsigned int num1, unsigned int num2, Player p) {
            smallNumber = std::min<unsigned int>(num1, num2);
            bigNumber   = std::max<unsigned int>(num1, num2);
            player      = p;
        }

        unsigned int smallNumber;
        unsigned int bigNumber;
        Player player;                                /* player that did the move that lead to this state */

};

//Helper methods
bool isTerminal(const State &state);
int maxValue(const State &state);
int minValue(const State &state);
std::vector<State> getSuccessors(const State &state);

int main() {
    std::string readLine;
#ifndef DOMJUDGE
    std::cout << "Reading numbers in..."  << std::endl;
    std::ifstream input;
    input.open("../input.txt");

    if (!input.is_open()) {
        std::cerr << "Unable to open file!" << std::endl;
        return -1;
    }

    std::getline(input, readLine);
#else
    std::getline(std::cin, readLine);
#endif

    int num1, num2;
    sscanf(readLine.c_str(), "%d %d", &num1, &num2);

    const State initial(num1, num2, NONE);

    if(isTerminal(initial)) {
        std::cout << "Stan wins" << std::endl;
    }

#ifndef DOMJUDGE
    std::cout << "Starting with numbers " << initial.smallNumber << " and " << initial.bigNumber << std::endl;
#endif

    unsigned int res = maxValue(initial);

    if (res == 1) {
        std::cout << "Stan wins" << std::endl;
    } else {
        std::cout << "Ollie wins" << std::endl;
    }

    return 0;
}

/**
 * Checks whether the given state is a terminal state, thus if the game is finished.
 * @param state State to check.
 * @return True in case of a terminal state, else false.
 */
bool isTerminal(const State &state) {
    if (state.smallNumber == 0 || state.bigNumber == 0) {
        return true;
    } else {
        return false;
    }
}

/**
 * Performs the MAX step for a given state.
 * @param state State to examine.
 * @return Maximized utility value.
 */
int maxValue(const State &state) {
    if (isTerminal(state)) {
        return (state.player == STAN) ? WIN_STAN : WIN_OLLIE;
    }

    std::vector<State> successors = getSuccessors(state);
    int currentMax = std::numeric_limits<int>::min();
    for (const auto &s : successors) {
        int val = minValue(s);
        if (val > currentMax) {
            currentMax = val;
        }
    }

    return currentMax;
}

/**
 * Performs the MIN step for a given state.
 * @param state State to examine.
 * @return Minimized utility value.
 */
int minValue(const State &state) {
    if (isTerminal(state)) {
        return (state.player == STAN) ? WIN_STAN : WIN_OLLIE;
    }

    std::vector<State> successors = getSuccessors(state);
    int currentMin = std::numeric_limits<int>::max();
    for (const auto &s : successors) {
        int val = maxValue(s);
        if (val < currentMin) {
            currentMin = val;
        }
    }

    return currentMin;
}


/**
 * Calculates all the successor state of the given state.
 * @param state State, for which the successors should be calculated.
 * @return Vector of the successor states.
 */
std::vector<State> getSuccessors(const State &state) {
    unsigned int numSuccessors = state.bigNumber / state.smallNumber;

    std::vector<State> successors;
    successors.reserve(numSuccessors);
    Player p = (state.player == STAN) ? OLLIE : STAN;

    for (unsigned int i = 1; i <= numSuccessors; i++) {
        successors.emplace_back(State(state.bigNumber - i*state.smallNumber, state.smallNumber, p));
    }

    return successors;
}