#include <string>
#include <stack>
#include <queue>
#include <algorithm>
#include <set>
#include <map>
#include <cmath>
#include <vector>
#include <memory>
#include <fstream>
#include <iostream>
#include <iomanip>
#include <stdio.h>
#include <cmath>
using namespace std;
using ll = long long;
using ull = unsigned long long;
using db = double;
using uint = unsigned int;


// Input and output stream settings
ifstream input("input.txt");
ofstream output("output.txt");


// Declatation of PhysicalItem class for correct compilation
class PhysicalItem;


// Abstract class Character that describes character behaviour
class Character {
private:
    // The number of HP of a character
    int healthPoints;
    // The name of a character
    string name;
public:
    // Method implementing taking of damage
    void takeDamage(int damage) {
        this->healthPoints -= damage;
    }
    // Method implementing healing
    void heal(int healValue) {
        this->healthPoints += healValue;
    }
    // Virtual method describing obtaining a Physical Item
    virtual void obtainItem(shared_ptr<PhysicalItem> item) {};
    // Virtual method describing loosing a Physical Item
    virtual void loseItem(shared_ptr<PhysicalItem> item) {};
    // Name getter
    string getName() {
        return this->name;
    }
    // HP getter
    int getHP() const {
        return this->healthPoints;
    }
    // Constructor for a non-const string name
    Character(int healthPoints, string& name) {
        this->healthPoints = healthPoints;
        this->name = name;
    }
    // Constructor for a const string name
    Character(int healthPoints, const string& name) {
        this->healthPoints = healthPoints;
        this->name = name;
    }
};


// Abstract class PhysicalItem that describes Physical Items behaviour
class PhysicalItem {
private:
    // Boolean flag showing if item is usable more than once
    bool isUsableOnce;
    // Name of a Physical Item
    string name;
protected:
    // Virtual method describing logic of item use
    virtual void useLogic(shared_ptr<Character> user, shared_ptr<Character> target) = 0;
public:
    // Method that implements the use of a Physical Item
    void use(shared_ptr<Character> user, shared_ptr<Character> target, shared_ptr< PhysicalItem> item) {
        useLogic(user, target);
        if (this->isUsableOnce) {
            user->loseItem(item);
        }
    }
    // Name getter
    string getName() {
        return this->name;
    }
    // Constructor
    PhysicalItem(string& name, bool isUsableOnce) {
        this->name = name;
        this->isUsableOnce = isUsableOnce;
    }
};


// Class implemeting weapon functional 
class Weapon : public PhysicalItem {
private:
    // The damage of a weapon
    int damage;
    // Method that implements the use logic of a weapon
    void useLogic(shared_ptr<Character> user, shared_ptr<Character> target) {
        target->takeDamage(this->damage);
    }
public:
    // Damage getter
    int getDamage() const {
        return this->damage;
    }
    // Constructor
    Weapon(int damage, string& name, shared_ptr<Character> owner) : PhysicalItem(name, false) {
        this->damage = damage;
    }
};


// Class implemeting potion functional 
class Potion : public PhysicalItem {
private:
    // The heal value of a potion
    int healValue;
    // Method that implements the use logic of a potion
    void useLogic(shared_ptr<Character> user, shared_ptr<Character> target) {
        target->heal(this->healValue);
    }
public:
    // Heal value getter
    int getHealValue() const {
        return this->healValue;
    }
    // Constructor
    Potion(int healValue, string& name, shared_ptr<Character> owner) : PhysicalItem(name, true) {
        this->healValue = healValue;
    }
};


// Class implemeting spell functional 
class Spell : public PhysicalItem {
private:
    // Vector of allowed targets for a spell
    vector <shared_ptr<Character>> allowedTargets;
    // Method that checks if a target is valid
    bool isAllowed(shared_ptr<Character> target) {
        for (auto it = this->allowedTargets.begin(); it != this->allowedTargets.end(); it++) {
            if ((*it) == target) {
                return true;
            }
        }
        return false;
    }
    // Method that implements the use logic of a spell
    void useLogic(shared_ptr<Character> user, shared_ptr<Character> target) {
        if (isAllowed(target)) {
            target->takeDamage(target->getHP());
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
public:
    // Method that adds new target for a spell
    void addNewTarget(shared_ptr<Character> target) {
        this->allowedTargets.push_back(target);
    }
    // Getter for a number of allowed targets
    int getNumAllowedTargets() {
        return this->allowedTargets.size();
    }
    // Constructor
    Spell(string& name, shared_ptr<Character> owner) : PhysicalItem(name, true) {};
};


// General Container template
template<typename T> class Container {
private:
    // The capacity of a container
    int maxCapacity;
protected:
    // Vector of pointers to elements of a Container
    vector <shared_ptr<T>> elements;
    // Method that returns an iterator to an element
    vector <shared_ptr<T>>::iterator find(shared_ptr<T> item) {
        for (auto it = elements.begin(); it != elements.end(); it++) {
            if (*it == item) {
                return it;
            }
        }
        return this->elements.end();
    }
public:
    // Method that removes an item from a Container
    void removeItem(shared_ptr<T> newItem) {
        auto it = this->find(newItem);
        if (it == this->elements.end()) {
            throw invalid_argument("Error caught");
        }
        this->elements.erase(it);
    }
    // Method that adds an item to a Container
    void addItem(shared_ptr<T> newItem) {
        if (this->maxCapacity == this->elements.size()) {
            throw invalid_argument("Error caught");
        }
        auto it = this->find(newItem);
        if (it != this->elements.end()) {
            throw invalid_argument("Error caught");
        }
        this->elements.push_back(newItem);
    }
    // Constructor
    Container(int maxCapacity) {
        this->maxCapacity = maxCapacity;
    }
};


// Concept for classes inherited from a PhysicalItem
template<typename T> concept DerivedPhysicalItem = is_base_of<PhysicalItem, T>::value;


// Abstract container for classes inherited from a PhysicalItem
template <DerivedPhysicalItem T> class Container<T> {
private:
    // The capacity of a container
    int maxCapacity;
protected:
    // Vector of pointers to elements of a Container 
    vector <shared_ptr<T>> elements;
    // Method that returns an iterator to an element 
    vector <shared_ptr<T>>::iterator find(shared_ptr<T> item) {
        for (auto it = elements.begin(); it != elements.end(); it++) {
            if (*it == item) {
                return it;
            }
        }
        return this->elements.end();
    }
public:
    // Method that returns a pointer to an item
    shared_ptr<T> getItem(string& name) {
        for (auto it = elements.begin(); it != elements.end(); it++) {
            if ((*it)->getName() == name) {
                return *it;
            }
        }
        throw invalid_argument("Error caught");
    }
    // Method that checks if an item is in a container
    bool contains(shared_ptr<T> item) {
        for (auto it = elements.begin(); it != elements.end(); it++) {
            if (*it == item) {
                return true;
            }
        }
        return false;
    }
    // Method that removes an item from a Container 
    void removeItem(shared_ptr<T> newItem) {
        auto it = this->find(newItem);
        if (it == this->elements.end()) {
            throw invalid_argument("Error caught");
        }
        this->elements.erase(it);
    }
    // Method that adds an item to a Container
    void addItem(shared_ptr<T> newItem) {
        if (this->maxCapacity == this->elements.size()) {
            throw invalid_argument("Error caught");
        }
        auto it = this->find(newItem);
        if (it != this->elements.end()) {
            throw invalid_argument("Error caught");
        }
        this->elements.push_back(newItem);
    }
    // Virtual method for showing all elements
    virtual void show() = 0;
    // Constructor
    Container(int maxCapacity) {
        this->maxCapacity = maxCapacity;
    }
};


// Container of Weapons
class Arsenal : public Container<Weapon> {
public:
    // Implemetns output of all contained elements
    void show() {
        sort(this->elements.begin(), this->elements.end(), [](shared_ptr<Weapon> a, shared_ptr<Weapon> b) { return a->getName() < b->getName(); });
        for (auto it = elements.begin(); it != elements.end(); it++) {
            output << (*it)->getName() << ":" << (*it)->getDamage() << " ";
        }
        output << "\n";
    }
    // Constructor
    Arsenal(int maxCapacity) : Container(maxCapacity) {};
};


// Container of Spells
class SpellBook : public Container<Spell> {
public:
    // Implemetns output of all contained elements
    void show() {
        sort(this->elements.begin(), this->elements.end(), [](shared_ptr<Spell> a, shared_ptr<Spell> b) { return a->getName() < b->getName(); });
        for (auto it = elements.begin(); it != elements.end(); it++) {
            output << (*it)->getName() << ":" << (*it)->getNumAllowedTargets() << " ";
        }
        output << "\n";
    }
    // Constructor
    SpellBook(int maxCapacity) : Container(maxCapacity) {};
};


// Container of Potions
class MedicalBag : public Container<Potion> {
public:
    // Implemetns output of all contained elements
    void show() {
        sort(this->elements.begin(), this->elements.end(), [](shared_ptr<Potion> a, shared_ptr<Potion> b) { return a->getName() < b->getName(); });
        for (auto it = elements.begin(); it != elements.end(); it++) {
            output << (*it)->getName() << ":" << (*it)->getHealValue() << " ";
        }
        output << "\n";
    }
    // Constructor
    MedicalBag(int maxCapactity) : Container(maxCapactity) {};
};


// Singletone Narrator character
class Narrator : public Character {
public:
    // Method to get the Narrator instance
    static shared_ptr<Narrator> getInstance() {
        if (narrator == nullptr) {
            narrator = shared_ptr<Narrator>(new Narrator());
        }
        return narrator;
    }
    // Copy constructor is removed
    Narrator(Narrator const&) = delete;
    // = operator is removed
    Narrator& operator=(Narrator const&) = delete;
private:
    // Pointer to the Narrator instance
    inline static shared_ptr<Narrator> narrator;
    Narrator() : Character(1000, "Narrator") {}
};


// Virtual class describing a Weapon User behavior
class WeaponUser : virtual public Character {
protected:
    // Container of weapons
    shared_ptr<Arsenal> arsenal;
public:
    // Method that adds a new weapon
    void addWeapon(shared_ptr<Weapon> weapon) {
        arsenal->addItem(weapon);
    }
    // Method that returns a poiner to a weapon
    shared_ptr<Weapon> getWeapon(string& name) {
        return this->arsenal->getItem(name);
    }
    // Method that removes a weapon
    void loseWeapon(shared_ptr<Weapon> weapon) {
        this->arsenal->removeItem(weapon);
    }
    // Method that implements the attack
    void attack(shared_ptr<Character> attacker, shared_ptr<Character> target, shared_ptr<Weapon> weapon) {
        weapon->use(attacker, target, weapon);
    }
    // Method that prints weapons
    void showWeapons() {
        this->arsenal->show();
    }
    // Constructor
    WeaponUser(int healthPoints, string& name, int weapons) : Character(healthPoints, name) {
        this->arsenal = make_shared<Arsenal>(weapons);
    }
};


// Virtual class describing a Potion User behavior
class PotionUser : virtual public Character {
protected:
    // Container of potions
    shared_ptr<MedicalBag> medicalBag;
public:
    // Method that adds a new potion
    void addPotion(shared_ptr<Potion> potion) {
        medicalBag->addItem(potion);
    }
    // Method that returns a poiner to a potion
    shared_ptr<Potion> getPotion(string& name) {
        return this->medicalBag->getItem(name);
    }
    // Method that removes a potion 
    void losePotion(shared_ptr<Potion> potion) {
        this->medicalBag->removeItem(potion);
    }
    // Method that implements the drinking of a potion
    void usePotion(shared_ptr<Character> user, shared_ptr<Character> target, shared_ptr<Potion> potion) {
        potion->use(user, target, potion);
    }
    // Method that prints potions
    void showPotions() {
        this->medicalBag->show();
    }
    // Constructor
    PotionUser(int healthPoints, string& name, int potions) : Character(healthPoints, name) {
        this->medicalBag = make_shared<MedicalBag>(potions);
    }
};


// Virtual class describing a Spell User behavior
class SpellUser : virtual public Character {
protected:
    // Container of spells
    shared_ptr<SpellBook> spellBook;
public:
    // Method that adds a new spell
    void addSpell(shared_ptr<Spell> spell) {
        spellBook->addItem(spell);
    }
    // Method that returns a poiner to a spell
    shared_ptr<Spell> getSpell(string& name) {
        return this->spellBook->getItem(name);
    }
    // Method that removes a spell 
    void loseSpell(shared_ptr<Spell> spell) {
        this->spellBook->removeItem(spell);
    }
    // Method that implements the cast of a spell
    void cast(shared_ptr<Character> caster, shared_ptr<Character> target, shared_ptr<Spell> spell) {
        spell->use(caster, target, spell);
    }
    // Method that prints spells
    void showSpells() {
        this->spellBook->show();
    }
    // Constructor
    SpellUser(int healthPoints, string& name, int spells) : Character(healthPoints, name) {
        this->spellBook = make_shared<SpellBook>(spells);
    }
};


// Figher class that inherits WeaponUser and PotionUser
class Fighter : public WeaponUser, public PotionUser {
private:
    // The number of weapons carried
    const static int maxWeapons = 3;
    // The number of potions carried
    const static int maxPotions = 5;
public:
    // Method that implemets obtaining a new item
    void obtainItem(shared_ptr<PhysicalItem> item) {
        if (dynamic_pointer_cast<Weapon>(item) != nullptr) {
            this->addWeapon(dynamic_pointer_cast<Weapon>(item));
        }
        else if (dynamic_pointer_cast<Potion>(item) != nullptr) {
            this->addPotion(dynamic_pointer_cast<Potion>(item));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    // Method that implemets losing an item
    void loseItem(shared_ptr<PhysicalItem> item) {
        if (dynamic_pointer_cast<Weapon>(item) != nullptr) {
            this->arsenal->removeItem(dynamic_pointer_cast<Weapon>(item));
        }
        else if (dynamic_pointer_cast<Potion>(item) != nullptr) {
            this->medicalBag->removeItem(dynamic_pointer_cast<Potion>(item));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    // Method that implements output of a Fighter
    void show() {
        output << this->getName() << ":fighter:" << this->getHP();
    }
    // Constructor
    Fighter(int initHP, string& name) : WeaponUser(initHP, name, maxWeapons), PotionUser(initHP, name, maxPotions), Character(initHP, name) {}
};


// Archer class that inherits WeaponUser, PotionUser and SpellUser
class Archer : public WeaponUser, public PotionUser, public SpellUser {
private:
    // The number of weapons carried 
    const static int maxWeapons = 2;
    // The number of potions carried
    const static int maxPotions = 3;
    // The number of spells carried
    const static int maxSpells = 2;
public:
    // Method that implemets obtaining a new item
    void obtainItem(shared_ptr<PhysicalItem> item) {
        if (dynamic_pointer_cast<Weapon>(item) != nullptr) {
            this->addWeapon(dynamic_pointer_cast<Weapon>(item));
        }
        else if (dynamic_pointer_cast<Potion>(item) != nullptr) {
            this->addPotion(dynamic_pointer_cast<Potion>(item));
        }
        else if (dynamic_pointer_cast<Spell>(item) != nullptr) {
            this->addSpell(dynamic_pointer_cast<Spell>(item));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    // Method that implemets losing an item
    void loseItem(shared_ptr<PhysicalItem> item) {
        if (dynamic_pointer_cast<Weapon>(item) != nullptr) {
            this->arsenal->removeItem(dynamic_pointer_cast<Weapon>(item));
        }
        else if (dynamic_pointer_cast<Potion>(item) != nullptr) {
            this->medicalBag->removeItem(dynamic_pointer_cast<Potion>(item));
        }
        else if (dynamic_pointer_cast<Spell>(item) != nullptr) {
            this->spellBook->removeItem(dynamic_pointer_cast<Spell>(item));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    // Method that implements output of an Archer
    void show() {
        output << this->getName() << ":archer:" << this->getHP();
    }
    // Constructor
    Archer(int initHP, string& name) : WeaponUser(initHP, name, maxWeapons), PotionUser(initHP, name, maxPotions), SpellUser(initHP, name, maxSpells), Character(initHP, name) {}
};


// Wizard class that inherits PotionUser and SpellUser 
class Wizard : public PotionUser, public SpellUser {
private:
    // The number of potions carried 
    const static int maxPotions = 10;
    // The number of spells carried
    const static int maxSpells = 10;
public:
    // Method that implemets obtaining a new item 
    void obtainItem(shared_ptr<PhysicalItem> item) {
        if (dynamic_pointer_cast<Potion>(item) != nullptr) {
            this->addPotion(dynamic_pointer_cast<Potion>(item));
        }
        else if (dynamic_pointer_cast<Spell>(item) != nullptr) {
            this->addSpell(dynamic_pointer_cast<Spell>(item));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    // Method that implemets losing an item
    void loseItem(shared_ptr<PhysicalItem> item) {
        if (dynamic_pointer_cast<Potion>(item) != nullptr) {
            this->medicalBag->removeItem(dynamic_pointer_cast<Potion>(item));
        }
        else if (dynamic_pointer_cast<Spell>(item) != nullptr) {
            this->spellBook->removeItem(dynamic_pointer_cast<Spell>(item));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    // Method that implements output of an Wizard
    void show() {
        output << this->getName() << ":wizard:" << this->getHP();
    }
    // Constructor
    Wizard(int initHP, string& name) : PotionUser(initHP, name, maxPotions), SpellUser(initHP, name, maxSpells), Character(initHP, name) {}
};


// Function that returns pointer to a character stored in the vector
shared_ptr<Character> getCharacter(vector <shared_ptr<Character>>& characters, string character) {
    for (auto it = characters.begin(); it != characters.end(); it++) {
        if ((*it)->getName() == character) {
            return *it;
        }
    }
    throw invalid_argument("Error caught");
}


// Function that checks if a character stored in the vector
bool findCharacter(vector <shared_ptr<Character>>& characters, string character) {
    for (auto it = characters.begin(); it != characters.end(); it++) {
        if ((*it)->getName() == character) {
            return true;
        }
    }
    return false;
}


// Function that inputs and adds a new weapon to a character
void newWeapon(vector <shared_ptr<Character>>& characters) {
    string ownerName;
    string weaponName;
    int damage;
    input >> ownerName >> weaponName >> damage;
    if (damage <= 0) {
        throw invalid_argument("Error caught");
    }
    shared_ptr<Character> owner = getCharacter(characters, ownerName);
    if (dynamic_pointer_cast<Fighter>(owner) != nullptr or dynamic_pointer_cast<Archer>(owner) != nullptr) {
        owner->obtainItem(shared_ptr<Weapon>(new Weapon(damage, weaponName, owner)));
    }
    else {
        throw invalid_argument("Error caught");
    }
    output << ownerName << " just obtained a new weapon called " << weaponName << ".\n";
}


// Function that inputs and adds a new potion to a character
void newPotion(vector <shared_ptr<Character>>& characters) {
    string ownerName;
    string potionName;
    int healValue;
    input >> ownerName >> potionName >> healValue;
    if (healValue <= 0) {
        throw invalid_argument("Error caught");
    }
    shared_ptr<Character> owner = getCharacter(characters, ownerName);
    if (dynamic_pointer_cast<Fighter>(owner) != nullptr or dynamic_pointer_cast<Archer>(owner) != nullptr or dynamic_pointer_cast<Wizard>(owner) != nullptr) {
        owner->obtainItem(shared_ptr<Potion>(new Potion(healValue, potionName, owner)));
    }
    else {
        throw invalid_argument("Error caught");
    }
    output << ownerName << " just obtained a new potion called " << potionName << ".\n";
}


// Function that inputs and adds a new spell to a character
void newSpell(vector <shared_ptr<Character>>& characters) {
    string ownerName;
    string spellName;
    int m;
    input >> ownerName >> spellName >> m;
    vector<string> targets;
    for (int i = 0; i < m; i++) {
        string targetName;
        input >> targetName;
        targets.push_back(targetName);
    }
    shared_ptr<Character> owner = getCharacter(characters, ownerName);
    shared_ptr<Spell> spell(new Spell(spellName, owner));
    for (int i = 0; i < m; i++) {
        shared_ptr<Character> target = getCharacter(characters, targets[i]);
        spell->addNewTarget(target);
    }
    if (dynamic_pointer_cast<Archer>(owner) != nullptr or dynamic_pointer_cast<Wizard>(owner) != nullptr) {
        owner->obtainItem(spell);
    }
    else {
        throw invalid_argument("Error caught");
    }
    output << ownerName << " just obtained a new spell called " << spellName << ".\n";
}


// Function that inputs and adds a new character to the vector
void newCharacter(vector <shared_ptr<Character>>& characters) {
    string type;
    string name;
    int initHP;
    input >> type >> name >> initHP;
    if (!findCharacter(characters, name)) {
        if (type == "fighter") {
            shared_ptr<Fighter> fighter(new Fighter(initHP, name));
            characters.push_back(dynamic_pointer_cast<Character>(fighter));
        }
        else if (type == "archer") {
            shared_ptr<Archer> archer(new Archer(initHP, name));
            characters.push_back(dynamic_pointer_cast<Character>(archer));
        }
        else if (type == "wizard") {
            shared_ptr<Wizard> wizard(new Wizard(initHP, name));
            characters.push_back(dynamic_pointer_cast<Character>(wizard));
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    else {
        throw invalid_argument("Error caught");
    }
    output << "A new " << type << " came to town, " << name << ".\n";
}


// Method that implemetns death of a character
void deathScenario(shared_ptr<Character> character, vector <shared_ptr<Character>>& characters) {
    if (character->getHP() > 0) {
        return;
    }
    if (character->getHP() <= 0) {
        output << character->getName() << " has died...\n";
        for (auto it = characters.begin(); it != characters.end(); it++) {
            if ((*it) == character) {
                characters.erase(it);
                return;
            }
        }
    }
    throw invalid_argument("Error caught");
}


// Method that implements the attack scenario
void attackScenario(vector <shared_ptr<Character>>& characters) {
    string attackerName;
    string targetName;
    string weaponName;
    input >> attackerName >> targetName >> weaponName;
    shared_ptr<WeaponUser> attacker = dynamic_pointer_cast<WeaponUser>(getCharacter(characters, attackerName));
    shared_ptr<Character> target = getCharacter(characters, targetName);
    if (attacker != nullptr) {
        shared_ptr<Weapon> weapon = attacker->getWeapon(weaponName);
        if (weapon != nullptr) {
            attacker->attack(attacker, target, weapon);
        }
        else {
            throw invalid_argument("Error caught");
        }
    }
    else {
        throw invalid_argument("Error caught");
    }
    output << attackerName << " attacks " << targetName << " with their " << weaponName << "!\n";
    deathScenario(target, characters);
}


// Method that implements the cast scenario
void castScenario(vector <shared_ptr<Character>>& characters) {
    string casterName;
    string targetName;
    string spellName;
    input >> casterName >> targetName >> spellName;
    shared_ptr<SpellUser> caster = dynamic_pointer_cast<SpellUser>(getCharacter(characters, casterName));
    shared_ptr<Character> target = getCharacter(characters, targetName);
    if (caster != nullptr) {
        shared_ptr<Spell> spell = caster->getSpell(spellName);
        caster->cast(caster, target, spell);
    }
    else {
        throw invalid_argument("Error caught");
    }
    output << casterName << " casts " << spellName << " on " << targetName << "!\n";
    deathScenario(target, characters);
}


// Method that implements the drink scenario
void drinkScenario(vector <shared_ptr<Character>>& characters) {
    string supplierName;
    string drinkerName;
    string potionName;
    input >> supplierName >> drinkerName >> potionName;
    shared_ptr<PotionUser> supplier = dynamic_pointer_cast<PotionUser>(getCharacter(characters, supplierName));
    shared_ptr<Character> drinker = getCharacter(characters, drinkerName);
    if (supplier != nullptr) {
        shared_ptr<Potion> potion = supplier->getPotion(potionName);
        supplier->usePotion(supplier, drinker, potion);
    }
    output << drinkerName << " drinks " << potionName << " from " << supplierName << ".\n";
    deathScenario(drinker, characters);
}


// Method that implements the dialogue scenario
void dialogueScenario(vector <shared_ptr<Character>>& characters) {
    string speakerName;
    int speechLength;
    vector<string> speech;
    input >> speakerName >> speechLength;
    for (int i = 0; i < speechLength; i++) {
        string line;
        input >> line;
        speech.push_back(line);
    }
    shared_ptr<Character> speaker = getCharacter(characters, speakerName);
    output << speaker->getName() << ": ";
    for (auto it = speech.begin(); it != speech.end(); it++) {
        output << (*it) << " ";
    }
    output << "\n";
}


// Method that outputs all characters alive
void showCharacters(vector <shared_ptr<Character>>& characters) {
    sort(characters.begin(), characters.end(), [](shared_ptr<Character> a, shared_ptr<Character> b) { return a->getName() < b->getName(); });
    for (auto it = characters.begin(); it != characters.end(); it++) {
        weak_ptr<Character> check = *it;
        if (!check.expired()) {
            shared_ptr<Character> character = *it;
            if (dynamic_pointer_cast<Fighter>(character) != nullptr) {
                dynamic_pointer_cast<Fighter>(character)->show();
            }
            else if (dynamic_pointer_cast<Archer>(character) != nullptr) {
                dynamic_pointer_cast<Archer>(character)->show();
            }
            else if (dynamic_pointer_cast<Wizard>(character) != nullptr) {
                dynamic_pointer_cast<Wizard>(character)->show();
            }
            else if (dynamic_pointer_cast<Narrator>(character) != nullptr) {
                continue;
            }
            output << " ";
        }
    }
    output << "\n";
}


// Method that outputs all weapons of a character
void showWeapons(vector <shared_ptr<Character>>& characters) {
    string characterName;
    input >> characterName;
    shared_ptr<WeaponUser> bearer = dynamic_pointer_cast<WeaponUser>(getCharacter(characters, characterName));
    if (bearer != nullptr) {
        bearer->showWeapons();
    }
    else {
        throw invalid_argument("Error caught");
    }
}


// Method that outputs all potions of a character
void showPotions(vector <shared_ptr<Character>>& characters) {
    string characterName;
    input >> characterName;
    shared_ptr<PotionUser> bearer = dynamic_pointer_cast<PotionUser>(getCharacter(characters, characterName));
    if (bearer != nullptr) {
        bearer->showPotions();
    }
    else {
        throw invalid_argument("Error caught");
    }
}


// Method that outputs all spells of a character
void showSpells(vector <shared_ptr<Character>>& characters) {
    string characterName;
    input >> characterName;
    shared_ptr<SpellUser> bearer = dynamic_pointer_cast<SpellUser>(getCharacter(characters, characterName));
    if (bearer != nullptr) {
        bearer->showSpells();
    }
    else {
        throw invalid_argument("Error caught");
    }
}


int main() {
    // The number of events
    int n;
    // The vector of characters
    vector <shared_ptr<Character>> characters;
    // Adding the Narrator
    characters.push_back(Narrator::getInstance());
    input >> n;
    for (int i = 0; i < n; i++) {
        try {
            string command;
            input >> command;
            // Creation of an instance
            if (command == "Create") {
                input >> command;
                if (command == "character") {
                    newCharacter(characters);
                }
                else if (command == "item") {
                    input >> command;
                    if (command == "weapon") {
                        newWeapon(characters);
                    }
                    else if (command == "potion") {
                        newPotion(characters);
                    }
                    else if (command == "spell") {
                        newSpell(characters);
                    }
                }
            }
            // Attack scenario
            else if (command == "Attack") {
                attackScenario(characters);
            }
            // Cast scenario
            else if (command == "Cast") {
                castScenario(characters);
            }
            // Drink scenario
            else if (command == "Drink") {
                drinkScenario(characters);
            }
            // Dialogue scenario
            else if (command == "Dialogue") {
                dialogueScenario(characters);
            }
            // Showing some instances
            else if (command == "Show") {
                input >> command;
                if (command == "characters") {
                    showCharacters(characters);
                }
                else if (command == "weapons") {
                    showWeapons(characters);
                }
                else if (command == "potions") {
                    showPotions(characters);
                }
                else if (command == "spells") {
                    showSpells(characters);
                }
            }
        }
        // Exceptions cather
        catch (const exception& ex) {
            output << ex.what() << "\n";
        }
    }
    return 0;
}

