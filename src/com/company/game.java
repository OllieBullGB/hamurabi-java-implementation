/**
 *      Filename: game.java
 *
 *      Author: Ollie Bull
 *
 *      Date: 15th October 2021
 *
 *      Description: A java implementation of the text-based strategy game Hamurabi
 *
 *      Version: v1.0 15/10/21
 */

package com.company;
import java.util.Scanner;

public class game {
    //game completion vars
    private static int year;
    private static boolean failed;

    //population control vars
    private static int population;
    private static int arrivals;
    private static int deaths;
    private static int starvations;
    private static double starvePercentageAverage;

    //farming vars
    private static int bushelsStored;
    private static int bushelsHarvested;
    private static int bushelsPerAcre;
    private static int acresOfLand;
    private static int acresOfLandCultivated;
    private static int bushelsRatFood;

    //natural occurance vars
    private static double plagueChance;
    private static boolean plague;
    private static double ratChance;
    private static boolean rats;

    //Initialise the game with the starting amount of resources and the starting
    //changes of natural occurrences
    public game()
    {

        year = 0;
        failed = false;

        population = 95;
        arrivals = 5;
        deaths = 0;
        starvations = 0;
        starvePercentageAverage = 0;

        bushelsHarvested = 3000;
        bushelsRatFood = 200;
        bushelsStored = 0;
        acresOfLand = 1000;
        acresOfLandCultivated = 500;

        plagueChance = 0;
        ratChance = 0.5;

        //while you haven't lost or hit a win parameter continue playing
        while(!failed)
        {
            completeYear();
        }

    }

    //Calls methods used in one turn of the game
    public static void completeYear()
    {
        System.out.println("============================================================================");
        System.out.println("It has been " + year + " years since the start of your term");
        System.out.println("============================================================================");

        calculatePlague();
        calculateHarvest();
        calculateRat();
        calculateArrivals();
        calculatePerformance();
        //if failed performance check exit out of game
        if(failed) return;
        showHarvest();
        showPopulation();
        landTrade();
        plantCrops();
        feedThePeople();
        year++;
    }

    //Display the statistics from last turns harvest and whether
    //a natural occurrence (rats) have decreased existing supplies
    private static void showHarvest()
    {
        bushelsStored = bushelsStored + bushelsHarvested - bushelsRatFood;
        if(bushelsStored < 0)
        {
            bushelsStored = 0;
        }
        System.out.println("The city now owns " + acresOfLand + " acres of farmland.");
        System.out.println("You harvested " + bushelsPerAcre+ " bushels per acre.");
        if(rats)
        {
            System.out.println("Rats ate " + bushelsRatFood + " bushels.");
        }
        System.out.println("You now have " + bushelsStored + " bushels in store.");
        System.out.println("============================================================================");
    }

    //Display the statistics from the current turn for population and whether
    //a natural occurrence (plague) has killed half of last turns population
    public static void showPopulation()
    {
        population = population + arrivals - (deaths + starvations);
        if(plague)
        {
            System.out.println("Hamurabi: A Horrible plague struck - half the people died!");
        }
        System.out.println("The population is now " + population);
        System.out.println("============================================================================");
    }

    //Opportunity to increase land owned in exchange for food by buying land
    //at a price which is random between 5 and 15 per acre each turn
    public static void landTrade()
    {
        Scanner scanner = new Scanner(System.in);
        int landPrice = (int)((Math.random()* 10) + 5);
        boolean valid;
        int offer;
        do
        {
            System.out.println("Hamurabi: Land is trading at " + landPrice + " bushels per acre");
            System.out.println("How many acres do you wish to buy/sell?");
            offer = scanner.nextInt();
            if(offer > 0)
            {
                int bushelsNeeded = offer * landPrice;
                if(bushelsStored >= bushelsNeeded)
                {
                    valid = true;
                    bushelsStored = bushelsStored - bushelsNeeded;
                    acresOfLand = acresOfLand + offer;
                }
                else
                {
                    valid = false;
                    System.out.println("Hamurabi: Think again. you only have " + bushelsStored + " bushels, now then");
                }
            }
            else if(offer < 0)
            {
                int bushelsGiven = (-offer) * landPrice;
                if(acresOfLand >= (-offer))
                {
                    valid = true;
                    bushelsStored = bushelsStored + bushelsGiven;
                    acresOfLand = acresOfLand + offer;
                }
                else
                {
                    valid = false;
                    System.out.println("Hamurabi: Think again. you only have " + acresOfLand + " acres, now then");
                }
            }
            else
            {
                valid = true;
            }
        } while(!valid);
        if(offer != 0)
        {
            System.out.println("Hamurabi: we have successfully bought / sold land");
        }
        System.out.println("============================================================================");
    }

    //Opportunity to plant crops with existing food supplies in exchange for
    //a yield per acre next turn, requires both population and land to
    //maintain and grow crops
    public static void plantCrops()
    {
        int acresUsed;
        int peopleUsed;
        int bushelsUsed;
        boolean valid;
        Scanner scanner = new Scanner(System.in);

        do
        {
            System.out.println("Hamurabi: How many acres do you wish to plant with seed?");
            acresUsed = scanner.nextInt();
            peopleUsed = (int)Math.ceil((double)acresUsed / 10);
            bushelsUsed = acresUsed * 2;

            if(peopleUsed <= population && bushelsUsed <= bushelsStored && acresUsed <= acresOfLand && acresUsed >= 0)
            {
                valid = true;
            }
            else
            {
                valid = false;
                if(peopleUsed > population)
                {
                    System.out.println("Hamurabi: Think again. You have only " + population + " people to tend the fields, now then");
                }
                if(bushelsUsed > bushelsStored)
                {
                    System.out.println("Hamurabi: Think again. You have only " + bushelsStored + " bushels of grain, now then");
                }
                if(acresUsed > acresOfLand || acresUsed < 0)
                {
                    System.out.println("Hamurabi: Think again. You have only " + acresOfLand + " acres of land, now then");
                }
            }

        } while(!valid);

        bushelsStored = bushelsStored - bushelsUsed;
        acresOfLandCultivated = acresUsed;

        System.out.println("You are cultivating " + acresOfLandCultivated + " acres");
        System.out.println("============================================================================");
    }

    //Opportunity to feed population with existing food supplies in order to prevent
    //starvation of the populace
    public static void feedThePeople()
    {
        int bushelsUsed;
        boolean valid;
        Scanner scanner = new Scanner(System.in);
        do
        {
            System.out.println("Hamurabi: How many bushels do you wish to feed to your people?");
            bushelsUsed = scanner.nextInt();
            if(bushelsUsed <= bushelsStored && bushelsUsed >= 0)
            {
                valid = true;
            }
            else
            {
                valid = false;
                System.out.println("Don't be stupid, you cant feed your population a negative amount of food");
            }
        } while(!valid);

        System.out.println("You have fed your people " + bushelsUsed + " bushels");
        System.out.println("============================================================================");

        starvations = (int)(population - Math.floor((double)bushelsUsed / 20));
        if(starvations < 0)
        {
            starvations = 0;
        }
        starvePercentageAverage = ((starvePercentageAverage * year) + ((double)starvations / (double)population))/ (year+1);
    }

    //Generates a random number and checks whether it is lower than the plague chance
    //if so it kills half the settlement's population
    private static void calculatePlague()
    {
        if(year > 1)
        {
            plagueChance = 0.15;
            double roll = Math.random();
            if(roll < plagueChance)
            {
                deaths = deaths + (int)(population/2);
                plague = true;
            }
        }
    }

    //Generates a random number anc checks whether it is lower than the rat chance
    //if so it removes a percentage of food from the stores
    private static void calculateRat()
    {
        if(year > 0)
        {
            double roll = Math.random();
            if(roll < ratChance)
            {
                double bushelsEatenPercentage = (Math.random()*20) / 100;
                bushelsRatFood = (int)(bushelsEatenPercentage * bushelsStored);
                rats = true;
            }
        }
    }

    //Uses the number of fields sown last turn as well as a random yield per acre
    //between 1 and 6 to calculate the yield of last turns harvest
    private static void calculateHarvest()
    {
        if(year > 0)
        {
            bushelsPerAcre = (int)(Math.random() * 6) + 1;
            bushelsHarvested = bushelsPerAcre * acresOfLandCultivated;
        }

    }

    //Calculates the number of new arrivals through immigration or birth that have
    //brought new people into your settlement, based on food stores, number of acres
    //owned and the current population of the city
    private static void calculateArrivals()
    {
        if(year > 0)
        {
            arrivals = ( (((int)(Math.random() * 6) + 1) * (20 * acresOfLand + bushelsStored) / population) /100 + 1);
        }
    }

    //Contains the win-lose scenarios for the game, if more than 45% of the population
    //starve in one turn you lose. If you survive for 10 turns you receive a
    //speech describing the opinions of the populace towards your 10-year term
    private static void calculatePerformance()
    {
        if(population <= 0)
        {
            System.out.println("All your citizens have died");
            failed = true;
        }

        double deathFactor = (double)starvations / (double)population;
        if( deathFactor > 0.45)
        {
            System.out.println("You starved " + starvations + " people in one year!!");
            System.out.println("You have been impeached and thrown out of office");
            failed = true;
        }

        if(year >= 10)
        {
            int acresPerPerson = acresOfLand / population;

            System.out.println("In your ten year term:");
            System.out.println(starvePercentageAverage + "% of people died per year");
            System.out.println("You started with 10 acres per person and ended with "
                    + acresPerPerson + " acres per person");

            if(starvePercentageAverage > 0.33 || acresPerPerson < 7)
            {
                System.out.println("You have been impeached due to extreme mismanagement");
            }
            else if(starvePercentageAverage > 0.1 || acresPerPerson < 9)
            {
                System.out.println("The people remaining find you an unpleasant rule and hate your guts");
            }
            else if(starvePercentageAverage > 0.03 || acresPerPerson < 10)
            {
                int populationHatedBy = (int)(((Math.random()*80) + 1) / 100) * population;
                System.out.println("Your performance could have been better but wasn't too bad. " +
                        + populationHatedBy + " people would like to see you assassinated by we" +
                        " all have our trivial problems");
            }
            else
            {
                System.out.println("A fantastic performance! Charlemagne, Disraeli" +
                        " and Jefferson could not have done better");
            }
            failed = true;
        }
    }

}
