package com.tp.F1WebSite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(path = "/register")
    public String register() {
        return "register";
    }

    @GetMapping(path = "/home")
    public String home() {
        return "home";
    }


    @GetMapping("/drivers")
    public String getDriversPage() {
        return "drivers";
    }

    @GetMapping("/drivers/{id}")
    public String getDriverDetailsPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("driverId", id);
        return "driver-details";
    }

    @GetMapping("/drivers/{id}/races")
    public String getDriverRacesPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("driverId", id);
        return "driver-races";
    }


    @GetMapping("/constructors")
    public String getConstructorsPage() {
        return "constructors";
    }

    @GetMapping("/constructors/{id}")
    public String getConstructorDetailsPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("constructorId", id);
        return "constructor-details";
    }

    @GetMapping("/constructors/{id}/races")
    public String getConstructorRacesPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("constructorId", id);
        return "constructor-races";
    }


    @GetMapping("/circuits")
    public String getCircuitsPage() {
        return "circuits";
    }

    @GetMapping("/circuits/{id}")
    public String getCircuitDetailsPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("circuitId", id);
        return "circuit-details";
    }


    @GetMapping("/races")
    public String getRacesPage() {
        return "races";
    }

    @GetMapping("/races/{id}")
    public String getRacesDetailsPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("raceId", id);
        return "race-details";
    }

    @GetMapping("/races/{id}/results")
    public String getRaceResultsPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("raceId", id);
        return "race-results";
    }

    @GetMapping("/races/{id}/qualifying")
    public String getRaceQualifyingPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("raceId", id);
        return "race-qualifying";
    }
}
