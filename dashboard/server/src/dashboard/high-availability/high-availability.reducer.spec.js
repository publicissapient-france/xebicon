import {expect} from "chai";
import {fromJS} from "immutable";
import reducer from "./high-availability.reducer";

describe('high availability reducer', () => {

  const initialState = fromJS({
    nodes: [
      {
        "name": "node-A",
        "state": "ON",
        "apps": [{"name": "app-name-1"}, {"name": "app-name-2"}, {"name": "app-name-3"}],
        "labels": [{"name": "label-1"}, {"name": "label-2"}]
      },
      {
        "name": "node-B",
        "state": "ON",
        "apps": [{"name": "app-name-10"}],
        "labels": [{"name": "label-10"}, {"name": "label-20"}]
      },
      {
        "name": "node-C",
        "state": "ON",
        "apps": [{"name": "app-name-100"}, {"name": "app-name-200"}, {"name": "app-name-300"}],
        "labels": [{"name": "label-100"}, {"name": "label-200"}]
      }
    ],
    shops: {"items": {"pauillac": 0, "margaux": 0, "pessac": 0}},
    buy: {"items": {"pauillac": 0, "margaux": 0, "pessac": 0}}
  });

  it('should return default state', () => {
    const nextState = reducer(undefined, {type: 'UNKNOWN'});

    expect(nextState.get('nodes').toJS()).to.deep.equal([{type: "cloud", active: true}, {type: "local", active: true}]);
    expect(nextState.get('shops').toJS()).not.to.be.undefined;
  });

  describe('K8S_STATUS', () => {

    it('should update nodes status without changing the shops status', () => {
      const nextState = reducer(initialState, {
        type: "K8S_STATUS", payload: [
          {
            "name": "node-A",
            "state": "ON",
            "apps": [{"name": "app-name-1"}, {"name": "app-name-2"}, {"name": "app-name-3"}],
            "labels": [{"name": "label-1"}, {"name": "label-2"}]
          },
          {
            "name": "node-C-2",
            "state": "ON",
            "apps": [{"name": "app-name-100"}, {"name": "app-name-200"}, {"name": "app-name-300"}],
            "labels": [{"name": "label-100"}, {"name": "label-200"}]
          }
        ]
      });

      expect(nextState.get('nodes').toJS()).to.deep.equal([
        {
          "name": "node-A",
          "state": "ON",
          "apps": [{"name": "app-name-1"}, {"name": "app-name-2"}, {"name": "app-name-3"}],
          "labels": [{"name": "label-1"}, {"name": "label-2"}]
        },
        {
          "name": "node-C-2",
          "state": "ON",
          "apps": [{"name": "app-name-100"}, {"name": "app-name-200"}, {"name": "app-name-300"}],
          "labels": [{"name": "label-100"}, {"name": "label-200"}]
        }
      ]);

      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 0, "pessac": 0}});
    });
  });

  describe('SHOP_STATE', () => {
    it('should update shops status without changing the nodes status', () => {
      const nextState = reducer(initialState, {
        type: "SHOP_STATE",
        payload: {"items": {"pauillac": 10, "margaux": 9, "pessac": 8}}
      });

      expect(nextState.get('nodes').toJS()).to.deep.equal([
        {
          "name": "node-A",
          "state": "ON",
          "apps": [{"name": "app-name-1"}, {"name": "app-name-2"}, {"name": "app-name-3"}],
          "labels": [{"name": "label-1"}, {"name": "label-2"}]
        },
        {
          "name": "node-B",
          "state": "ON",
          "apps": [{"name": "app-name-10"}],
          "labels": [{"name": "label-10"}, {"name": "label-20"}]
        },
        {
          "name": "node-C",
          "state": "ON",
          "apps": [{"name": "app-name-100"}, {"name": "app-name-200"}, {"name": "app-name-300"}],
          "labels": [{"name": "label-100"}, {"name": "label-200"}]
        }
      ]);

      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 10, "margaux": 9, "pessac": 8}});
    });

    it('should not update shops status when no payload', () => {
      const nextState = reducer(initialState, {type: "SHOP_STATE"});
      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 0, "pessac": 0}});
    });

    it('should not update shops status when no items', () => {
      const nextState = reducer(initialState, {type: "SHOP_STATE", payload: {}});
      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 0, "pessac": 0}});
    });

    it('should update shops status when only one known item', () => {
      const nextState = reducer(initialState, {
        type: "SHOP_STATE",
        payload: {items: {"margaux": 12}}
      });
      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 12, "pessac": 0}});
    });

    it('should update shops status only for known items', () => {
      const nextState = reducer(initialState, {
        type: "SHOP_STATE",
        payload: {items: {"pauillac": 1, "plop": 10, "berk": 20}}
      });
      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 1, "margaux": 0, "pessac": 0}});
    });

    it('should reset shops status on KEYNOTE_STATE.AVAILABILITY_START', () => {
      const startState = initialState.setIn(['shops', 'items'], fromJS({"pauillac": 10, "margaux": 12, "pessac": 5}));
      const nextState = reducer(startState, {type: "KEYNOTE_STATE", payload: {value: "AVAILABILITY_START"}});
      expect(nextState.get('shops').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 0, "pessac": 0}});
    });

  });

  describe('BUY', () => {
    it('should update buy status BEER + 1', () => {
      const nextState = reducer(initialState, {type: "BUY", payload: {type: "pauillac"}});
      expect(nextState.get('buy').toJS()).to.deep.equal({"items": {"pauillac": 1, "margaux": 0, "pessac": 0}});
    });

    it('should update buy status SANDWICH + 1', () => {
      const nextState = reducer(initialState, {type: "BUY", payload: {type: "margaux"}});
      expect(nextState.get('buy').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 1, "pessac": 0}});
    });

    it('should not update buy status when UNKNOWN', () => {
      const nextState = reducer(initialState, {type: "BUY", payload: {type: "UNKNOWN"}});
      expect(nextState.get('buy').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 0, "pessac": 0}});
    });

    it('should reset buy status on KEYNOTE_STATE.AVAILABILITY_START', () => {
      const startState = initialState.setIn(['buy', 'items'], fromJS({"pauillac": 10, "margaux": 12, "pessac": 5}));
      const nextState = reducer(startState, {type: "KEYNOTE_STATE", payload: {value: "AVAILABILITY_START"}});
      expect(nextState.get('buy').toJS()).to.deep.equal({"items": {"pauillac": 0, "margaux": 0, "pessac": 0}});
    });
  });
});
