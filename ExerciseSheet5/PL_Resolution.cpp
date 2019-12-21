/**
 * @file    PL_Resolution.cpp
 * @authors Carolin, Dominik
 * @brief   Solution to sheet 5, exercise 4 of the course Introduction to Artificial Intelligence.
 */

#include <iostream>
#include <string>
#include <set>
#include <vector>
#include <cmath>
#include <fstream>                                    /* allows reading of files                          */

//#define DOMJUDGE                                    /* uncomment this to enable DomJugde mode           */

enum AtomOption {                                     /* possible occurence types of an atom              */
    NONE=0, POSITIV=1, NEGATIV=2
};

class Clause {
    public:
        /**
         * Constructor
         * @param size Number of possible different atoms in the term.
         */
        explicit Clause(unsigned int size) {
            atomTerms.assign(size, NONE);
        }

        /**
         * Setter for the i-th atom.
         * @param index Index of the atom to set.
         * @param o     Option of the atom, e.g. NONE if it's not part of the term, NEGATIV if it's negated, otherwise
         *              POSITIV.
         */
        void setAtom(unsigned int index, AtomOption o) {
            atomTerms.at(index - 1) = o;
        }

        /**
         * Getter for the i-th atom.
         * @param  index Index of the term, e.g. 1 for a.
         * @return NONE if the atom is not part of the term, NEGATIV if it's negated and else POSITIV.
         */
        AtomOption getAtom(unsigned int index) const {
            return atomTerms.at(index - 1);
        }

        /**
         * Getter for the length of the clause.
         * @return Length of the clause.
         */
        unsigned int getNumberOfAtoms() const {
            return atomTerms.size();
        }

        /**
         * Checks whether this clause is an empty clause.
         * @return True if it's an empty clause, else false.
         */
        bool isEmptyClause() const {
            for (unsigned int i = 1; i < atomTerms.size(); i++) {
                if (getAtom(i) != NONE) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Overloaded operator for the output.
         * @param os Output stream to write to.
         * @param c  Clause to output.
         * @return Modified output stream.
         */
        friend std::ostream& operator<< (std::ostream& os, const Clause& c) {
            for (unsigned int i = 1; i <= c.getNumberOfAtoms(); i++) {
                switch (c.getAtom(i)) {
                    case POSITIV:
                        os << char(i - 1 + 'a') << " ";
                        break;

                    case NEGATIV:
                        os << "-" << char(i - 1 + 'a') << " ";
                        break;

                    default:
                        break;
                }
            }

            return os;
        }

        /**
         * Overloaded compare operator to be able to use a set for the clauses.
         * @param c1 First clause.
         * @param c2 Second clause
         * @return True if c1 < c2, otherwise false.
         */
        friend bool operator< (const Clause &c1, const Clause &c2) {
            long sumC1 = 0;
            long sumC2 = 0;

            for (unsigned int i = 1; i < c1.getNumberOfAtoms(); i++) {
                if (c1.getAtom(i) == POSITIV) {
                    sumC1 += std::pow(3, i);
                } else if (c1.getAtom(i) == NEGATIV) {
                    sumC1 += 2*std::pow(3, i);
                }
            }

            for (unsigned int i = 1; i < c2.getNumberOfAtoms(); i++) {
                if (c2.getAtom(i) == POSITIV) {
                    sumC2 += std::pow(3, i);
                } else if (c2.getAtom(i) == NEGATIV) {
                    sumC2 += 2*std::pow(3, i);
                }
            }

            return sumC1 < sumC2;
        }

    private:
        std::vector<AtomOption> atomTerms;
};

                                                      /* Helper methods                                   */
std::set<Clause> readProblem();
bool isInconsistent(std::set<Clause> clauses);
Clause plResolve(const Clause &c1, const Clause &c2, bool &resolved);

/**
 * Overload output operator for a set of clauses.
 * @param os      Output stream to write to.
 * @param clauses Clauses to output.
 * @return Modified output stream.
 */
std::ostream& operator<< (std::ostream& os, const std::set<Clause> &clauses) {
    os << "\nSet of clauses: " << std::endl;
    for (const auto &c : clauses) {
        os << "{ " << c << "}" << " ";
    }
    os << "\n";
    return os;
}


int main() {
    std::set<Clause> clauses = readProblem();

#ifndef DOMJUDGE
    std::cout << clauses << std::endl;
#endif

    if (isInconsistent(clauses)) {
        std::cout << "true" << std::endl;
    } else {
        std::cout << "false" << std::endl;
    }

    return 0;
}

/**
 * Reads the problem from in and constructs the corresponding set of clauses.
 * @return Set of clauses.
 */
std::set<Clause> readProblem() {
    std::string   line;
    bool          clauseMode = false;
    unsigned int  numClauses = 0;
    unsigned int  numAtoms   = 0;

    std::set<Clause> clauses;
#ifndef DOMJUDGE                                      /* support laziness by reading from a file          */
    std::ifstream input;
    input.open("../input.txt");

    if (!input.is_open()) {
        std::cerr << "Unable to open file!" << std::endl;
        std::exit(-1);
    }
#endif

    while (true) {
#ifndef DOMJUDGE
        std::getline(input, line);
#else                                                 /* read from std::cin (only for DomJugde)           */
        std::getline(std::cin, line);
#endif

        if (line[0] == 'c') {                         /* ignore comments                                  */
            continue;
        } else if (!clauseMode) {                     /* still searching for information about number of
                                                       * number of clauses                                */
            if (line[0] == 'p') {
                sscanf(line.c_str(), "p cnf %d %d", &numAtoms, &numClauses);
                if (numAtoms == 0 || numClauses == 0) {
                    std::exit(-1);
                }
                clauseMode = true;
            }
        } else {                                      /* reading in the clauses                           */
            Clause c(numAtoms);

            auto start = 0U;
            auto end   = line.find(' ');
            unsigned int pos;
            AtomOption   opt;

            while (end != std::string::npos) {
                int atom = std::stoi(line.substr(start, end - start));

                pos = std::abs(atom);
                if (pos > numAtoms || pos == 0) {
                    continue;
                }
                opt = (atom > 0) ? POSITIV : NEGATIV;

                c.setAtom(pos, opt);

                start = end + 1;
                end = line.find(' ', start);
            }

            clauses.insert(c);

            numClauses--;
            if(numClauses == 0) {
                break;
            }
        }
    }

    return clauses;
}

bool isInconsistent(std::set<Clause> clauses) {
    unsigned int oldSize = 0;
    bool resolveSuccess  = false;
    std::set<Clause> newClauses;

    while (true) {
        for (const auto &c1 : clauses) {
            for (const auto &c2 : clauses) {
                Clause resolvent = plResolve(c1, c2, resolveSuccess);
                if (resolveSuccess) {
                    if (resolvent.isEmptyClause()) {
                        return true;
                    }

                    newClauses.insert(resolvent);
                }
            }
        }

        oldSize = clauses.size();
        clauses.insert(newClauses.begin(), newClauses.end());
        if (clauses.size() <= oldSize) {
            return false;
        }
    }
}

/**
 * Applies resolution calculus to the given clauses.
 * @param c1       First clause.
 * @param c2       Second clause.
 * @param resolved Used for returning resolving state, if it's false the returned clause is invalid.
 * @return Resolved clause in case of resolved is true, otherwise an invalid clause.
 */
Clause plResolve(const Clause &c1, const Clause &c2, bool &resolved) {
    unsigned int length = std::max(c1.getNumberOfAtoms(), c2.getNumberOfAtoms());
    Clause result(length);
    resolved = false;

    for (unsigned int i = 1; i < length; i++) {
        AtomOption a1 = c1.getAtom(i);
        AtomOption a2 = c2.getAtom(i);
        if (a1 == a2) {
            result.setAtom(i, a1);
        } else {
            if (a1 != NONE && a2 != NONE) {
                result.setAtom(i, NONE);
                resolved = true;
            } else {
                if (a1 == NONE) {
                    result.setAtom(i, a2);
                } else {
                    result.setAtom(i, a1);
                }
            }
        }
    }

    return result;
}