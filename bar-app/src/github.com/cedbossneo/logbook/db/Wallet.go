package db

import (
	"github.com/cedbossneo/logbook/types"
	r "gopkg.in/dancannon/gorethink.v2"
	"log"
)

func (db *Database) GetWallet(user string) (types.Wallet, error) {
	var wallet types.Wallet
	err := r.Table("wallets").Filter(map[string]interface{}{
		"user": user,
	}).ReadOne(&wallet, db.Session)
	if err != nil {
		log.Println("Unable to retrieve wallet for user", user, err)
		return types.Wallet{}, err
	}
	return wallet, nil
}

func (db *Database) WalletExist(user string) (bool, error) {
	var wallet types.Wallet
	err := r.Table("wallets").Filter(map[string]interface{}{
		"user": user,
	}).ReadOne(&wallet, db.Session)
	if err == r.ErrEmptyResult {
		return false, nil
	} else if err != nil {
		log.Println("Unable to retrieve wallet for user", user, err)
		return false, err
	}
	return true, nil
}

func (db *Database) GetWallets() ([]types.Wallet, error) {
	var wallets []types.Wallet
	err := r.Table("wallets").ReadAll(&wallets, db.Session)
	if err == r.ErrEmptyResult {
		return []types.Wallet{}, nil
	} else if err != nil {
		log.Println("Unable to retrieve wallets", err)
		return []types.Wallet{}, err
	}
	return wallets, nil
}

func (db *Database) CreateWallet(user types.User) (types.Wallet, error) {
	wallet := types.Wallet{
		User: user,
		Amount: 20,
	}
	_, err := r.Table("wallets").Insert(wallet).RunWrite(db.Session)
	if err != nil {
		log.Println("Unable to insert wallet", err)
		return types.Wallet{}, err
	}
	return wallet, nil
}

func (db *Database) RemoveFromWallet(wallet types.Wallet, amount int) (bool, error) {
	if wallet.Amount < amount {
		log.Println("Not enough money in wallet")
		return false, nil
	}
	_, err := r.Table("wallets").Filter(map[string]interface{}{
		"user": wallet.UserId,
	}).Update(map[string]interface{}{
		"amount": r.Row.Field("amount").Sub(amount),
	}).RunWrite(db.Session)
	if err != nil {
		log.Println("Unable to update wallet", err)
		return false, err
	}
	return true, nil
}